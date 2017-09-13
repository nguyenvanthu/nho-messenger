package com.nho.server.processors.channel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.chat.router.impl.CheckChannelProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.channel.LeaveChannelRequest;
import com.nho.message.response.channel.LeaveChannelResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChannelException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.BotNho;
import com.nho.server.statics.ChatCounter;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.LEAVE_CHANNEL })
public class LeaveChannelProcessor extends AbstractNhoProcessor<LeaveChannelRequest> {

	@Override
	protected void process(LeaveChannelRequest request) {
		try {
			LeaveChannelResponse response = new LeaveChannelResponse();
			User user = getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user not login in LeaveChannelProcessor");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			if (request.getChannelId() == null) {
				getLogger().debug("leave channel request null");
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			getLogger().debug("user {} leave channel {} ", user.getUserName(), request.getChannelId());
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				whenLeaveChatWithBot(user, request.getSessionId());
				return;
			}
			this.getUserManager().removeUserInChannelPushed(user.getUserName(), request.getChannelId());
			ChatCounter.removePushTime(user.getUserName());
			ChatCounter.removePushTimeDraw(user.getUserName());

			response.setSuccessful(true);
			response.setUserName(user.getUserName());
			Set<String> subcribers = getUserInChannelWhenUserLeave(user.getUserName(), request.getChannelId());
			this.sendToUserNames(response, subcribers);
			deleteLiveObjectInChannel(request.getChannelId());
			logWhenLeaveChannel(user.getUserName(), subcribers, request.getSessionId());
		} catch (Exception exception) {
			throw new ChannelException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	private void logWhenLeaveChannel(String userName, Set<String> subscribes, String sessionId) {
		String subNames = "";
		for (String sub : subscribes) {
			subNames += sub + ",";
		}
		String content = "user " + userName + " leave channel when chat with " + subNames;
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.LEAVE_CHANNEL, userName, sessionId);
	}

	private void logWhenLeaveChatWithBot(String userName, String sessionId) {
		String content = "user " + userName + " leave channel when chat with bot";
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.LEAVE_CHANNEL_WITH_BOT, userName, sessionId);
	}

	/**
	 * get list user in channel when user A leave channel send command to
	 * {@link com.nho.chat.router.impl.LeaveChannelProcessor}
	 */
	private Set<String> getUserInChannelWhenUserLeave(String userName, String channelId) {
		Set<String> subcribers = new HashSet<>();
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.LEAVE_CHANNEL.getCode());
		data.setString(ChatField.CHANNEL_ID, channelId);
		data.setString(ChatField.LEAVER_USER, userName);

		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			int status = result.getInteger(ChatField.STATUS);
			if (status == 0) {
				PuArray array = result.getPuArray(ChatField.SUBCRIBE);
				for (PuValue value : array) {
					subcribers.add(value.getString());
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
		}
		return subcribers;
	}

	private void whenLeaveChatWithBot(User user, String sessionId) {
		// remove user in list user chat with boot
		this.getUserManager().removeUserChatWithBot(user.getUserName());
		getLogger().debug("remove user {} from list chat with bot ", user.getUserName());
		// remove interaction with boot
		BotNho.removeInteractionOfUser(user.getUserName());
		// remove timeChat with boot
		PuObject removeTimeChat = new PuObject();
		removeTimeChat.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_TIME.getCode());
		removeTimeChat.setString(ChatField.USER_NAME, user.getUserName());
		//
		BotNho.updateStateUserChatWithBot(user.getUserName(), false);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, removeTimeChat);
		// remove wait time chat with boot
		PuObject removeWaitTime = new PuObject();
		removeWaitTime.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_WAIT_TIME.getCode());
		removeWaitTime.setString(ChatField.USER_NAME, user.getUserName());
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, removeWaitTime);
		// remove live object is blocked by user
		PuObject removeLiveObjer = new PuObject();
		removeLiveObjer.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_OBJ_BY_USER.getCode());
		removeLiveObjer.setString(ChatField.USER_NAME, user.getUserName());
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, removeLiveObjer);
		// remove data message user chat with boot
		PuObject removeUserChatBot = new PuObject();
		removeUserChatBot.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_DATA_MSG_BOT.getCode());
		removeUserChatBot.setString(ChatField.USER_NAME, user.getUserName());
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, removeUserChatBot);
		logWhenLeaveChatWithBot(user.getUserName(), sessionId);
	}

	/**
	 * delete live objects in channel send command
	 * {@link ChannelCommand#DELTE_LIVE_OBJ} to
	 * {@link com.nho.chat.router.impl.DeleteLiveObjectProcessor}
	 */
	private void deleteLiveObjectInChannel(String channelId) {
		if (isChannelEmpty(channelId)) {
			getLogger().debug("send command delete live object in channel");
			PuObject deleteLiveObjData = new PuObject();
			deleteLiveObjData.setInteger(ChatField.COMMAND, ChannelCommand.DELTE_LIVE_OBJ.getCode());
			deleteLiveObjData.setString(ChatField.CHANNEL_ID, channelId);
			this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, deleteLiveObjData);
		}
	}

	/**
	 * check channel is empty send command {@link ChannelCommand#CHECK_CHANNEL}
	 * to {@link CheckChannelProcessor}
	 */
	private boolean isChannelEmpty(String channelId) {
		boolean isEmpty = false;
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.CHECK_CHANNEL.getCode());
		data.setString(ChatField.CHANNEL_ID, channelId);

		PuObject result = (PuObject) getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		if (result.getInteger(ChatField.STATUS) == 0) {
			isEmpty = result.getBoolean(ChatField.IS_EMPTY);
		}
		return isEmpty;
	}
}
