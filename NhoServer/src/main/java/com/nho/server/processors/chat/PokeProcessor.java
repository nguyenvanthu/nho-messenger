package com.nho.server.processors.chat;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.chat.router.impl.UpdatePingTimesProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.channel.PokeRequest;
import com.nho.message.response.channel.PokeReponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChatException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.helper.NotificationHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;
import com.nho.statics.PNF;
import com.nho.statics.PushNotificationType;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.POKE_REQUEST })
public class PokeProcessor extends AbstractNhoProcessor<PokeRequest> {

	@Override
	protected void process(PokeRequest request) {
		try {
			PokeReponse response = new PokeReponse();
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				response.setSuccess(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			NotificationHelper notiHelper = new NotificationHelper(getContext());
			getLogger().debug("channelId is " + request.getChannelId());
			UserMongoBean senderBean = this.getUserMongoModel().findByFacebookId(request.getFrom());
			String senderDisplayName = request.getFrom();
			if (senderBean != null) {
				senderDisplayName = senderBean.getDisplayName();
			}
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				pokeWithBot(user, request.getChannelId(), senderDisplayName);
				return;
			}
			int pingTime = updatePingTimes(user.getUserName(), request.getChannelId());
			getLogger().debug("ping time: " + pingTime);
			String receiverName = helper.getUsersInChannelByChannelId(request.getChannelId(), request.getFrom())
					.iterator().next();
			int friendPingTime = getPingTimes(receiverName, request.getChannelId());
			getLogger().debug("friend ping times: " + friendPingTime);
			User receiver = this.getUserManager().getUserByUserName(receiverName);
			if (receiver == null || this.getUserManager().isUserLogout(receiver.getUserName())) {
				getLogger().debug("receiver null ");
				response.setSuccess(false);
				response.setError(Error.USER_LOGOUT);
				this.send(response, request.getSessionId());
				return;
			}
			// push notification
			pushNotification(request.getChannelId(), receiver, user.getUserName(), senderDisplayName, notiHelper,
					friendPingTime, pingTime);
			sendPokeResponse(user, request.getChannelId(), senderDisplayName, pingTime, friendPingTime);
			sendLogPoke(user.getUserName(), receiverName, request.getSessionId());
		} catch (Exception exception) {
			throw new ChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	private void sendPokeResponse(User user, String channelId, String senderDisplayName, int pingTimes,
			int friendPingTimes) {
		getLogger().debug("send poke response");
		PokeReponse response = new PokeReponse();
		response.setChannelId(channelId);
		response.setSenderName(user.getUserName());
		response.setSenderDisplayName(senderDisplayName);
		response.setSuccess(true);
		response.setPingTimes(pingTimes);
		response.setFriendPingTimes(friendPingTimes);
		getLogger().debug("");
		this.sendToUser(response, user);
	}

	private void pokeWithBot(User user, String channelId, String senderDisplayName) {
		getLogger().debug("user {} poke with bot ", user.getUserName());
		PokeReponse response = new PokeReponse();
		response.setChannelId(channelId);
		response.setSenderName(user.getUserName());
		response.setSenderDisplayName(senderDisplayName);
		response.setSuccess(true);
		this.send(response, user.getSessions());
	}

	private void pushNotification(String channelId, User receiver, String from, String senderDisplayName,
			NotificationHelper notiHelper, int pingTimes, int friendPingTimes) {
		getLogger().debug("start send PN in Poke to user " + receiver.getUserName());
		String message = getMessagePushNotification(PushNotificationType.POKE, from, channelId, receiver.getUserName(),
				senderDisplayName, pingTimes, friendPingTimes);
		getLogger().debug("number session: {} ", receiver.getSessions().size());
		if (receiver.getSessions().size() > 0) {
			if (receiver.getSessions().size() == 1) {
				String sessionId = receiver.getSessions().iterator().next();
				getLogger().debug("push to session {}", sessionId);
				if (this.getUserManager().isDeviceInApp(sessionId)) {
					notiHelper.pushInApp(message, "poke", receiver, PushNotificationType.POKE);
				} else {
					getLogger().debug("send push via gcm");
					String deviceToken = this.getUserManager().getDeviceTokenBySessionId(sessionId);
					notiHelper.pushByGCM(message, receiver.getUserName(), deviceToken, "poke");
				}
			} else {
				// > 2 sessions , if have any session , send by gcm
				if (isNotInApp(receiver)) {
					getLogger().debug("send push via gcm");
					String deviceToken = this.getUserManager()
							.getDeviceTokenBySessionId(receiver.getSessions().iterator().next());
					notiHelper.pushByGCM(message, receiver.getUserName(), deviceToken, "poke");
				} else {
					notiHelper.pushInApp(message, "poke", receiver, PushNotificationType.POKE);
				}
			}
		} else {
			getLogger().debug("send push via gcm");
			notiHelper.pushByGCM(message, receiver.getUserName(), "", "poke");
		}
	}

	private boolean isNotInApp(User user) {
		boolean isNotIn = false;
		for (String sessionId : user.getSessions()) {
			if (!this.getUserManager().isDeviceInApp(sessionId)) {
				isNotIn = true;
			}
		}
		return isNotIn;
	}

	private String getMessagePushNotification(PushNotificationType type, String senderName, String channelId,
			String receiverName, String senderDisplayName, int pingTimes, int friendPingTimes) {
		PuObject message = new PuObject();
		message.setString(PNF.SENDER_NAME, senderName);
		message.setString(PNF.RECEIVER_NAME, receiverName);
		message.setString(PNF.SENDER_DISPLAY_NAME, senderDisplayName);
		message.setString(PNF.CHANNEL_ID, channelId);
		message.setInteger(PNF.PUSH_NOTIFICATION_TYPE, type.getCode());
		message.setInteger(PNF.PING_TIMES, pingTimes);
		message.setInteger(PNF.FRIEND_PING_TIMES, friendPingTimes);
		getLogger().debug(message.toJSON());
		return message.toJSON();
	}

	private void sendLogPoke(String userName, String receiverName, String sessionId) {
		String content = "user " + userName + " poke to " + receiverName;
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.POKE, userName, sessionId);
	}

	/**
	 * update ping times in channel send command to
	 * {@link UpdatePingTimesProcessor}
	 * 
	 * @param userName
	 * @param channelId
	 * @return
	 */
	private int updatePingTimes(String userName, String channelId) {
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.UPDATE_PING_TIMES.getCode());
		data.setString(ChatField.CHANNEL_ID, channelId);
		data.setString(ChatField.USER_NAME, userName);

		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			int status = result.getInteger(ChatField.STATUS);
			if (status == 0) {
				int pingTimes = result.getInteger(ChatField.PING_TIMES);
				return pingTimes;
			}

		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
		}
		return 0;
	}

	private int getPingTimes(String userName, String channelId) {
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.GET_PING_TIMES.getCode());
		data.setString(ChatField.CHANNEL_ID, channelId);
		data.setString(ChatField.USER_NAME, userName);

		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			int status = result.getInteger(ChatField.STATUS);
			if (status == 0) {
				int pingTimes = result.getInteger(ChatField.PING_TIMES);
				return pingTimes;
			}

		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
		}
		return 0;
	}
}
