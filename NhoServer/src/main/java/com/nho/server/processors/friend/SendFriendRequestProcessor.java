package com.nho.server.processors.friend;

import com.nhb.common.data.PuObject;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.SendFriendRequest;
import com.nho.message.response.friend.SendFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.helper.NotificationHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;
import com.nho.statics.PNF;
import com.nho.statics.PushNotificationType;
import com.nho.uams.statics.ActivityType;

/**
 * process request {@link SendFriendRequest} return response
 * {@link SendFriendResponse} send command
 * {@link FriendCommand#SEND_FRIEND_REQUEST} to
 * {@link com.nho.friend.router.impl.SendFriendRequestProcessor}
 */
@NhoCommandProcessor(command = { MessageType.SEND_FRIEND_REQUEST })
public class SendFriendRequestProcessor extends AbstractNhoProcessor<SendFriendRequest> {
	private boolean isUserExist(String userName) {
		boolean isExist = false;
		UserMongoBean user = this.getUserMongoModel().findByUserName(userName);
		if (user != null) {
			getLogger().debug("user is exist in db ");
			isExist = true;
		}
		return isExist;
	}

	@Override
	protected void process(SendFriendRequest request) {
		try {
			User user = getUserManager().getUserBySessionId(request.getSessionId());

			SendFriendResponse response = new SendFriendResponse();
			if (user == null) {
				getLogger().debug("error user not login in sendFriendRequestProcesscor");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			getLogger().debug("send new friend request from {} to {}", user.getUserName(),
					request.getReceiverUserName());
			if (isUserExist(request.getReceiverUserName()) == false
					|| isUserExist(request.getSenderUserName()) == false) {
				getLogger().debug("error user receiver is not exist");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_EXIST);
				this.send(response, request.getSessionId());
				return;
			}
			NotificationHelper notiHelper = new NotificationHelper(getContext());
			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.SEND_FRIEND_REQUEST.getCode());
			data.setString(FriendField.SENDER_NAME, request.getSenderUserName());
			data.setString(FriendField.RECEIVER_NAME, request.getReceiverUserName());
			data.setInteger(FriendField.STATUS, request.getStatusFriend().ordinal());

			PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
			int status = result.getInteger(FriendField.STATUS);
			if (status == 0) {
				response.setSuccessful(true);
				UserMongoBean senderBean = this.getUserMongoModel().findByUserName(request.getSenderUserName());
				UserMongoBean receiverBean = this.getUserMongoModel().findByUserName(request.getReceiverUserName());
				response.setStatusFriend(request.getStatusFriend());
				response.setSenderUserName(senderBean.getUserName());
				response.setSenderDisplayName(senderBean.getDisplayName());
				response.setAvatarSenderName(senderBean.getAvatar().getName());

				response.setReceiverDisplayName(receiverBean.getDisplayName());
				response.setReceiverUserName(receiverBean.getUserName());
				response.setAvatarReceiverName(receiverBean.getAvatar().getName());
				getLogger().debug("send friend response to " + request.getReceiverUserName());
				User receiver = this.getUserManager().getUserByUserName(request.getReceiverUserName());
				this.sendToUserName(response, request.getReceiverUserName());
				getLogger().debug("send friend response back sender");
				this.sendToUser(response, user);
				pushNotification(notiHelper, receiver, response);
				sendLogSendFriendRequest(request);
			} else {
				response.setSuccessful(false);
				response.setError(Error.fromCode(result.getInteger(FriendField.ERROR)));
				this.send(response, request.getSessionId());
				return;
			}
		} catch (Exception exception) {
			throw new FriendException(exception.getMessage(), exception.getCause(), this.getContext());
		}

	}

	private void pushNotification(NotificationHelper notiHelper, User receiver, SendFriendResponse response) {
		// push notification
		getLogger().debug("start push notification send friend request to user " + receiver.getUserName());
		String message = getContentPushNotification(PushNotificationType.SEND_FRIEND_REQUEST, response);
		getLogger().debug("message " + message);
		if (receiver.getSessions().size() > 0) {
			for (String sessionId : receiver.getSessions()) {
				if (this.getUserManager().isDeviceInApp(sessionId)) {
					getLogger().debug("send push api send friend request to " + receiver.getUserName());
					notiHelper.pushInApp(message, "sendFriendRequest", receiver,
							PushNotificationType.SEND_FRIEND_REQUEST);
				} else {
					getLogger().debug("send push notification to " + receiver.getUserName());
					String deviceToken = this.getUserManager().getDeviceTokenBySessionId(sessionId);
					notiHelper.pushByGCM(message, receiver.getUserName(), deviceToken, "sendFriendRequest");
				}
			}
		} else {
			notiHelper.pushByGCM(message, receiver.getUserName(), "", "sendFriendRequest");
		}

	}

	private String getContentPushNotification(PushNotificationType type, SendFriendResponse data) {
		PuObject message = new PuObject();
		message.setString(PNF.SENDER_NAME, data.getSenderUserName());
		message.setString(PNF.SENDER_DISPLAY_NAME, data.getSenderDisplayName());
		message.setString(PNF.AVATAR_SENDER, data.getAvatarSenderName());
		message.setString(PNF.RECEIVER_NAME, data.getReceiverUserName());
		message.setString(PNF.RECEIVER_DISPLAY_NAME, data.getReceiverDisplayName());
		message.setString(PNF.AVATAR_RECEIVER, data.getAvatarReceiverName());
		message.setInteger(PNF.PUSH_NOTIFICATION_TYPE, type.getCode());
		return message.toJSON();
	}

	private void sendLogSendFriendRequest(SendFriendRequest request) {
		String content = "send friend request to " + request.getReceiverUserName();
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.SEND_FRIEND_REQUEST, request.getSenderUserName(),
				request.getSessionId());
	}
}
