package com.nho.server.processors.channel;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.chat.router.impl.UpdateChannelProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.channel.CreateChannelRequest;
import com.nho.message.response.channel.ChatInvitationResponse;
import com.nho.message.response.channel.InvitedToChatEvent;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.ChannelType;
import com.nho.statics.Error;
import com.nho.statics.StatusUserInChannel;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.CREATE_CHANNEL })
public class CreateChannelProcessor extends AbstractNhoProcessor<CreateChannelRequest> {

	@Override
	protected void process(CreateChannelRequest request) {
		ChannelType channelType = request.getChannelType();
		User user = getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null) {
			whenUserNotLogin(request);
			return;
		}
		String channelId = createChannel(request, user);
		getLogger().debug("user {} create channel {}",user.getUserName(),channelId);
		switch (channelType) {
		case PRIVATE:
			if (request.getInvitedUsers().size() > 1) {
				getLogger().debug("error in create private channel : invited user must = 1 ");
				ChatInvitationResponse chatInvitationResponse = new ChatInvitationResponse();
				chatInvitationResponse.setSuccessful(false);
				chatInvitationResponse.setError(Error.INVITED_USER_INVALID);
				this.send(chatInvitationResponse, request.getSessionId());
				return;
			}
			String invitedUserName = request.getInvitedUsers().iterator().next();
			User invitedUser = getUserManager().getUserByUserName(invitedUserName);
			getLogger().debug("invited user is " + invitedUserName);
			if (invitedUser == null) {
				return;
			}
			StatusUserInChannel status = getStatusUserInChannel(channelId,invitedUserName);
			if (status != null) {
				whenInviteUserFail(channelId, request, user, status);
				return;
			}
			UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(user.getUserName());
			String displayName = user.getUserName();
			if (userBean == null) {
				getLogger().debug("userBean null ");
			}
			displayName = userBean.getDisplayName();
			sendInvitedChatToReceiver(invitedUser, user, displayName, channelId, request);
			sendLogCreateChannelWithFriend(invitedUserName, user.getUserName(), request.getSessionId());
			break;
		default:
			ChatInvitationResponse chatInvitationResponse = new ChatInvitationResponse();
			chatInvitationResponse.setSuccessful(false);
			chatInvitationResponse.setError(Error.CHANNEL_TYPE_INVALID);
			this.send(chatInvitationResponse, request.getSessionId());
			break;
		}
	}

	private void sendLogCreateChannelWithFriend(String friend, String userName, String sessionId) {
		String content = "user " + userName + " start chat with " + friend;
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.CREATE_CHANNEL_WITH_FRIEND, userName, sessionId);
	}

	/**
	 * get status {@link StatusUserInChannel} of user invitedUserName when chat
	 * with user userName
	 * 
	 * @param invitedUserName
	 * @param userName
	 * @return
	 */
	private StatusUserInChannel getStatusUserInChannel(String channelId,String invitedUserName) {
		ChannelHelper helper = new ChannelHelper(getContext());
		if (helper.isUserLogout(invitedUserName)) {
			getLogger().debug("invited user logout ");
			return StatusUserInChannel.INVITED_USER_LOG_OUT;
		}
		if (helper.isUserOffline(invitedUserName)) {
			getLogger().debug("invited user offline ");
			return StatusUserInChannel.INVITED_USER_OFFLINE;
		}
		if (helper.isUserChatWithBot(invitedUserName)) {
			getLogger().debug("error when create channel : invitedUser is chatting with bot ");
			return StatusUserInChannel.USER_CHAT_WITH_BOT;
		}
		if (helper.isUserBusy(channelId, invitedUserName)) {
			getLogger().debug("error when create channel : invitedUser is buzy ");
			return StatusUserInChannel.INVITED_USER_BUSY;
		}
		return null;
	}

	/**
	 * send {@link InvitedToChatEvent} message to receiver when create channel
	 * success
	 */
	private void sendInvitedChatToReceiver(User invitedUser, User user, String senderDisplayName, String channelId,
			CreateChannelRequest request) {
		InvitedToChatEvent invitedToChatEvent = new InvitedToChatEvent();
		invitedToChatEvent.setSender(user.getUserName());
		invitedToChatEvent.setSenderDisplayName(senderDisplayName);
		invitedToChatEvent.setMessage(request.getMessage());
		invitedToChatEvent.setChannelId(channelId);
		invitedToChatEvent.setTheme(request.getTheme());
		invitedToChatEvent.setPersonality(request.getPersonality());

		for (String sessionId : invitedUser.getSessions()) {
			if (this.getUserManager().isDeviceInApp(sessionId)) {
				this.send(invitedToChatEvent, sessionId);
				getLogger().debug("send to invite " + invitedUser.getUserName());
				whenInvitedUserSuccess(channelId, request, user, StatusUserInChannel.INVITED_USER_AVAILABLE);
			} else {
//				this.send(invitedToChatEvent, sessionId);
				getLogger().debug("invited user on pause");
				whenInvitedUserSuccess(channelId, request, user, StatusUserInChannel.INVITED_USER_ON_PAUSE);
			}
		}
	}

	/**
	 * create channel between two user send command
	 * {@link ChannelCommand#CREATE_CHANNEL} to
	 * {@link com.nho.chat.router.impl.CreateChannelProcessor}
	 * 
	 * @return channelId
	 */
	private String createChannel(CreateChannelRequest request, User user) {
		String invitedUserName = request.getInvitedUsers().iterator().next();
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.CREATE_CHANNEL.getCode());
		data.setString(ChatField.INVITER_USER, user.getUserName());
		data.setString(ChatField.INVITED_USER, invitedUserName);
		data.setInteger(ChatField.THEME, request.getTheme().getCode());
		data.setInteger(ChatField.PERSONALITY, request.getPersonality().getCode());
		String channelId = "";
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		result.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		channelId = result.getString(ChatField.CHANNEL_ID);
		getLogger().debug("result channelId from ChatServer is " + channelId);
		return channelId;
	}

	/**
	 * send error when user not login
	 */
	private void whenUserNotLogin(CreateChannelRequest request) {
		getLogger().debug("error user not login in CreateChannel Processor");
		ChatInvitationResponse chatInvitationResponse = new ChatInvitationResponse();
		chatInvitationResponse.setSuccessful(false);
		chatInvitationResponse.setError(Error.USER_NOT_LOGGED_IN);
		this.send(chatInvitationResponse, request.getSessionId());
	}

	/**
	 * send {@link ChatInvitationResponse} when create channel fail with reason
	 * {@link StatusUserInChannel}
	 */
	private void whenInviteUserFail(String channelId, CreateChannelRequest request, User user,
			StatusUserInChannel reason) {
		getLogger().debug("channelId is " + channelId);
		getLogger().debug("reason: " + reason);
		ChatInvitationResponse response = new ChatInvitationResponse();
		response.setSuccessful(true);
		response.setStatusChannel(reason);
		response.setChannelId(channelId);
		response.addInvitedUsers(request.getInvitedUsers());
		response.setMessage(request.getMessage());
		sendLogWhenCreateChatFail(user, request, reason);
		this.sendToUser(response, user);
	}

	private void sendLogWhenCreateChatFail(User user, CreateChannelRequest request, StatusUserInChannel reason) {
		String invitedUser = "";
		for (String invited : request.getInvitedUsers()) {
			invitedUser = invited + ", ";
		}
		String content = "user " + user.getUserName() + " cannot chat with " + invitedUser + " because " + reason;
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.CREATE_CHANNEL_FAIL, user.getUserName(),
				request.getSessionId());
	}

	/**
	 * send {@link ChatInvitationResponse} when create channel success
	 */
	private void whenInvitedUserSuccess(String channelId, CreateChannelRequest request, User user,
			StatusUserInChannel userStatus) {
		ChatInvitationResponse chatInvitationResponse = new ChatInvitationResponse();
		chatInvitationResponse.setSuccessful(true);
		chatInvitationResponse.setStatusChannel(userStatus);
		chatInvitationResponse.setChannelId(channelId);
		chatInvitationResponse.addInvitedUsers(request.getInvitedUsers());
		chatInvitationResponse.setMessage(request.getMessage());

		this.sendToUser(chatInvitationResponse, user);
		updateChannel(channelId);
	}

	/**
	 * update last time chat && times chat to {@link UpdateChannelProcessor}
	 * 
	 * @param channelId
	 */
	private void updateChannel(String channelId) {
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.UPDATE_CHANNEL.getCode());
		data.setString(ChatField.CHANNEL_ID, channelId);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
	}
}
