package com.nho.server.processors.friend;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.router.impl.AcceptFriendProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.AcceptFriendRequest;
import com.nho.message.response.friend.AcceptFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.helper.NotificationHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;
import com.nho.statics.PNF;
import com.nho.statics.PushNotificationType;
import com.nho.uams.statics.ActivityType;

/**
 * process request {@link AcceptFriendRequest} return response
 * {@link AcceptFriendResponse} send command {@link FriendCommand#ACCEPT_FRIEND}
 * to {@link AcceptFriendProcessor}
 */
@NhoCommandProcessor(command = { MessageType.ACCEPT_FRIEND_REQUEST })
public class AcceptFriendRequestProcessor extends AbstractNhoProcessor<AcceptFriendRequest> {

	@Override
	protected void process(AcceptFriendRequest request) {
		try {
			AcceptFriendResponse response = new AcceptFriendResponse();
			User user = getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user not login in AcceptFriendProcessor");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			NotificationHelper notiHelper = new NotificationHelper(getContext());
			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.ACCEPT_FRIEND.getCode());
			data.setString(FriendField.SENDER_NAME, request.getSenderUserName());
			data.setString(FriendField.ACCEPTER_NAME, request.getAccepterUserName());
			data.setInteger(FriendField.STATUS, request.getStatusFriend().ordinal());

			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if (status == 0) {
					User sender = this.getUserManager().getUserByUserName(request.getSenderUserName());
					UserMongoBean accepterBean = this.getUserMongoModel().findByUserName(request.getAccepterUserName());
					UserMongoBean senderBean = this.getUserMongoModel().findByUserName(request.getSenderUserName());
					response.setSuccessful(true);
					response.setAccepterDisplayName(accepterBean.getDisplayName());
					response.setAccepterUserName(accepterBean.getUserName());
					response.setAvatarAccepterName(accepterBean.getAvatar().getName());
					response.setStatusFriend(request.getStatusFriend());

					response.setSenderUserName(senderBean.getUserName());
					response.setSenderDisplayName(senderBean.getDisplayName());
					response.setAvatarSenderName(senderBean.getAvatar().getName());
					this.sendToUser(response, sender);
					this.sendToUser(response, user);
					pushNotification(notiHelper, sender, response);
					sendLogAcceptFriend(request);
				} else {
					response.setSuccessful(false);
					response.setError(Error.fromCode(result.getInteger(FriendField.ERROR)));
					this.send(response, request.getSessionId());
					return;
				}

			} catch (InterruptedException | ExecutionException e) {
				getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
				response.setSuccessful(false);
				response.setError(Error.ERROR_RABBIT_MQ);
				this.send(response, request.getSessionId());
				return;
			}
		} catch (Exception exception) {
			throw new FriendException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	private void pushNotification(NotificationHelper notiHelper, User sender, AcceptFriendResponse response) {
		// send push notification
		getLogger().debug("start send PN Accept Friend request to user " + sender.getUserName());
		String message = getMessagePushNotification(PushNotificationType.ACCEPT_FRIEND_REQUEST, response);
		if (sender.getSessions().size() > 0) {
			for (String sessionId : sender.getSessions()) {
				if (this.getUserManager().isDeviceInApp(sessionId)) {
					getLogger().debug("send push api accept friend request");
					notiHelper.pushInApp(message, "acceptFriendRequest", sender,
							PushNotificationType.ACCEPT_FRIEND_REQUEST);
				} else {
					String deviceToken = this.getUserManager().getDeviceTokenBySessionId(sessionId);
					notiHelper.pushByGCM(message, sender.getUserName(), deviceToken, "acceptFriendRequest");
				}
			}
		} else {
			notiHelper.pushByGCM(message, sender.getUserName(), "", "acceptFriendRequest");
		}
	}

	private String getMessagePushNotification(PushNotificationType type, AcceptFriendResponse data) {
		PuObject message = new PuObject();
		message.setString(PNF.SENDER_NAME, data.getSenderUserName());
		message.setString(PNF.SENDER_DISPLAY_NAME, data.getSenderDisplayName());
		message.setString(PNF.AVATAR_SENDER, data.getAvatarSenderName());
		message.setString(PNF.ACCEPTER_NAME, data.getAccepterUserName());
		message.setString(PNF.ACCEPTER_DISPLAY_NAME, data.getAccepterDisplayName());
		message.setString(PNF.AVATAR_ACCEPTER, data.getAvatarAccepterName());
		message.setInteger(PNF.PUSH_NOTIFICATION_TYPE, type.getCode());

		return message.toJSON();
	}

	private void sendLogAcceptFriend(AcceptFriendRequest request) {
		String content = "accepted friend request from user " + request.getSenderUserName();
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.ACCEPT_FRIEND, request.getAccepterUserName(),
				request.getSessionId());
	}
}
