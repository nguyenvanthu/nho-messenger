package com.nho.server.processors.channel;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.chat.data.UserInChannelBean;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.channel.JoinChannelRequest;
import com.nho.message.response.channel.JoinChannelResponse;
import com.nho.message.response.channel.JoinedToChannelResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;
import com.nho.statics.Personality;
import com.nho.statics.StatusUserInChannel;
import com.nho.statics.Theme;

@NhoCommandProcessor(command = { MessageType.JOIN_CHANNEL })
public class JoinChannelProcessor extends AbstractNhoProcessor<JoinChannelRequest> {
	private boolean isInviteChatUserBusy = false ;
	@Override
	protected void process(JoinChannelRequest request) {
		User user = getUserManager().getUserBySessionId(request.getSessionId());
		JoinChannelResponse response = new JoinChannelResponse();
		if (user == null || request.getChannelId() == null) {
			response.setSuccessful(false);
			response.setError(Error.USER_NOT_LOGGED_IN);
			this.send(response, request.getSessionId());
			return;
		}
		UserInChannelBean inviteChatUser = getUserInviteChat(request, user);
		if (inviteChatUser == null) {
			getLogger().debug("inviteChat User null");
			return;
		}
		getLogger().debug("user {} join to channel {}", user.getUserName(), request.getChannelId());
		response.setSuccessful(true);
		response.setChannelId(request.getChannelId());
		response.setPersonality(inviteChatUser.getPersonality());
		response.setTheme(inviteChatUser.getTheme());
		response.setUserInviteChat(inviteChatUser.getUserName());
		getLogger().debug("status invite chat user "+getStatusUserInviteChat(inviteChatUser.getUserName(), user.getUserName()));
		response.setStatusChannel(getStatusUserInviteChat(inviteChatUser.getUserName(), user.getUserName()));
		getLogger().debug("send JoinChannelResponse to user {}",user.getUserName());
		this.sendToUser(response, user);
		JoinedToChannelResponse joinedToChannelResponse = new JoinedToChannelResponse();
		joinedToChannelResponse.setChannelId(request.getChannelId());
		joinedToChannelResponse.setUserJoin(user.getUserName());
		joinedToChannelResponse.setPersonality(request.getPersonality());
		joinedToChannelResponse.setSuccessful(true);
		joinedToChannelResponse.setTheme(request.getTheme());
		User inviteUser = this.getUserManager().getUserByUserName(inviteChatUser.getUserName());
		if (inviteUser != null) {
			getLogger().debug("send message joinedToChannelResponse to user {}",inviteUser.getUserName());
			this.sendToUser(joinedToChannelResponse, inviteUser);
		}
	}

	/**
	 * get rest user when join channel send command
	 * {@link ChannelCommand#JOIN_CHANNEL} to
	 * {@link com.nho.chat.router.impl.JoinChannelProcessor}
	 */
	private UserInChannelBean getUserInviteChat(JoinChannelRequest request, User user) {
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.JOIN_CHANNEL.getCode());
		data.setString(ChatField.CHANNEL_ID, request.getChannelId());
		data.setString(ChatField.JOINER_USER, user.getUserName());
		data.setInteger(ChatField.THEME, request.getTheme().getCode());
		data.setInteger(ChatField.PERSONALITY, request.getPersonality().getCode());
		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			result.setType(ChatField.INVITER_USER, PuDataType.STRING);
			UserInChannelBean inviteChatUser = new UserInChannelBean();
			inviteChatUser.setUserName(result.getString(ChatField.INVITER_USER));
			inviteChatUser.setTheme(Theme.fromCode(result.getInteger(ChatField.THEME)));
			inviteChatUser.setPersonality(Personality.fromCode(result.getInteger(ChatField.PERSONALITY)));
			isInviteChatUserBusy = result.getBoolean(ChatField.IS_BUSY);
			getLogger().debug("is invitechat user busy : "+isInviteChatUserBusy);
			return inviteChatUser;
		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
			return null;
		}
	}

	private StatusUserInChannel getStatusUserInviteChat(String userInviteChat, String userJoin) {
		ChannelHelper helper = new ChannelHelper(getContext());
		if (helper.isUserLogout(userInviteChat)) {
			getLogger().debug("inviteChatUser user logout ");
			return StatusUserInChannel.INVITED_USER_LOG_OUT;
		}
		if (helper.isUserOffline(userInviteChat)) {
			getLogger().debug("inviteChatUser user offline ");
			return StatusUserInChannel.INVITED_USER_OFFLINE;
		}
		if (helper.isUserChatWithBot(userInviteChat)) {
			getLogger().debug("inviteChatUser is chatting with bot ");
			return StatusUserInChannel.USER_CHAT_WITH_BOT;
		}
		if (isInviteChatUserBusy) {
			getLogger().debug("inviteChatUser is buzy ");
			return StatusUserInChannel.INVITED_USER_BUSY;
		}
		return StatusUserInChannel.INVITED_USER_AVAILABLE;
	}
}
