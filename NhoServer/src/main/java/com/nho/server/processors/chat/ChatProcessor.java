package com.nho.server.processors.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuObject;
import com.nho.chat.router.impl.StoreDataMsgProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.ChatMessage;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.chat.NhoPoint;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChatException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.NotificationHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.BotNho;
import com.nho.server.statics.ChatCounter;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;
import com.nho.statics.PNF;
import com.nho.statics.PushNotificationType;

@NhoCommandProcessor(command = { MessageType.CHAT })
public class ChatProcessor extends AbstractNhoProcessor<ChatMessage> {

	@Override
	protected void process(ChatMessage request) {
		try {
			String sessionId = request.getSessionId();
			User user = getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null || request.getTo() == null) {
				getLogger().debug("error user null");
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			NotificationHelper notiHelper = new NotificationHelper(getContext());
			if (helper.isChannelChatWithBot(request.getTo())) {
				copyFromBot(request, user);
				return;
			}
			Set<String> userInChannelOnlines = helper.getOnlineUsersInChannel(request.getTo(), request.getFrom());
			getLogger().debug("number userOnlines in channel "+userInChannelOnlines.size());
			if (userInChannelOnlines.size() > 0) {
				boolean isReceiverBuzy = helper.isReceiverIsBusy(request.getTo(), request.getFrom());
				getLogger().debug("receiver is buzy:  " + isReceiverBuzy);
				if (!isReceiverBuzy) {
					getLogger().debug("start send chat message id " + request.getMessageId());
					request.setFrom(user.getUserName());
					getLogger().debug("send messsage chat to receiver ");
					for (String u : userInChannelOnlines) {
						User userOnline = this.getUserManager().getUserByUserName(u);
						this.send(request, userOnline.getSessions());
					}
					pushNotificationChat(notiHelper, request, userInChannelOnlines);
					getLogger().debug("send message chat done , channelId: " + request.getTo());
				} else {
					getLogger().debug("cannot send chat message because reciver is buzy");
					request.setError(Error.RECEIVER_BUZY);
					this.send(request, sessionId);
					getLogger().debug("send Push Notification to user buzys ");
					List<String> userBusys = helper.getUserBusysInChannel(request.getTo(), request.getFrom());
					pushNotificationToBusyUser(notiHelper, userBusys, request, user.getUserName());
				}
			} else {
				getLogger().debug("error user receiver chat message offline ");
				request.setError(Error.USER_OFFLINE);
				this.send(request, sessionId);
				sendPushNotificationToOfflineUser(helper, notiHelper, request, user.getUserName());
			}
		} catch (Exception exception) {
			throw new ChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	private void sendPushNotificationToOfflineUser(ChannelHelper helper, NotificationHelper notiHelper,
			ChatMessage request, String userName) {
		String message = getMessagePushNotification(PushNotificationType.CHAT_MESSAGE, request);
		if (ChatCounter.isPushNotification(userName)) {
			getLogger().debug("send Push Notification to user offlines ");
			List<String> userOfflines = helper.getUserOfflinesInChannel(request.getTo(), request.getFrom());
			for (String userOffline : userOfflines) {
				if (!isUserInChannelPushed(userOffline, request.getTo())) {
					User offline = this.getUserManager().getUserByUserName(userOffline);
					if (offline != null) {
						if (offline.getSessions().size() > 0) {
							for (String session : offline.getSessions()) {
								String deviceToken = this.getUserManager().getDeviceTokenBySessionId(session);
								notiHelper.pushByGCM(message, userOffline, deviceToken, "chat");
							}
						} else {
							notiHelper.pushByGCM(message, userOffline, "", "chat");
						}

					}
					this.getUserManager().addUserInChannelPushed(userOffline, request.getTo());
				}
			}
		}

	}

	private void pushNotificationToBusyUser(NotificationHelper notiHelper, List<String> userBusys, ChatMessage request,
			String userName) {
		String message = getMessagePushNotification(PushNotificationType.CHAT_MESSAGE, request);
		if (ChatCounter.isPushNotification(userName)) {
			for (String userBusy : userBusys) {
				User busyUser = this.getUserManager().getUserByUserName(userBusy);
				if (busyUser.getSessions().size() > 0) {
					for (String session : busyUser.getSessions()) {
						if (this.getUserManager().isDeviceInApp(session)) {
							getLogger().debug("send push api chat to busy user");
							notiHelper.pushInApp(message, "chat", busyUser, PushNotificationType.CHAT_MESSAGE);
						} else {
							String deviceToken = this.getUserManager().getDeviceTokenBySessionId(session);
							notiHelper.pushByGCM(message, userBusy, deviceToken, "chat");
						}
					}
				} else {
					notiHelper.pushByGCM(message, userBusy, "", "chat");
				}
			}
		}
	}
	
	private void copyFromBot(ChatMessage request, User user){
		getLogger().debug("user {} chat with bot ", user.getUserName());
		// after 10s , boot interact with user
		if (!BotNho.isUserCanChatWithBot(user.getUserName())) {
			PuObject dataGetTime = new PuObject();
			dataGetTime.setInteger(ChatField.COMMAND, ChannelCommand.GET_TIME_CHAT_WITH_BOT.getCode());
			dataGetTime.setString(ChatField.USER_NAME, user.getUserName());
			PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, dataGetTime);
			int status = result.getInteger(ChatField.STATUS);
			if (status == 0) {
				long timeChat = result.getLong(ChatField.TIME);
				if (timeChat == 0) {
					// store time chat'
					getLogger().debug("timechat = 0 , bot is not reply user");
					storeTimeChatOfUser(user);
				} else {
					long currentTimeChat = System.currentTimeMillis();
					if (currentTimeChat - timeChat < BotNho.TIME_BOT) {
						// store time chat
						long waitTime = currentTimeChat - timeChat;
						getLogger().debug("time chat is {}  , bot is not reply user", waitTime);
						storeTimeChatOfUser(user);
					} else {
						BotNho.updateStateUserChatWithBot(user.getUserName(), true);
						sendMessageFromBot(request);
					}
				}
			}
		} else {
			sendMessageFromBot(request);
		}
	}

	@SuppressWarnings("unused")
	private void chatWithBot(ChatMessage request, User user) {
		String dataMessage = getDataMessageChat(request);
		storeDataMessageChat(dataMessage, user.getUserName());
	}
	/**
	 * store data of message chat from user to boot 
	 * call to {@link StoreDataMsgProcessor}
	 */
	private void storeDataMessageChat(String dataMessage,String userName){
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.STORE_DATA_MSG.getCode());
		data.setString(ChatField.USER_NAME, userName);
		data.setString(ChatField.DATA_MESSAGE, dataMessage);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
	}

	private void storeTimeChatOfUser(User user) {
		PuObject storeTime = new PuObject();
		storeTime.setInteger(ChatField.COMMAND, ChannelCommand.STORE_TIME.getCode());
		storeTime.setString(ChatField.USER_NAME, user.getUserName());
		storeTime.setLong(ChatField.TIME, System.currentTimeMillis());
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, storeTime);
	}

	private void sendMessageFromBot(ChatMessage message) {
		User user = this.getUserManager().getUserBySessionId(message.getSessionId());
		message.setFrom(BotNho.USER_NAME);
		message.setSentTime(System.currentTimeMillis());
		String messageData = getDataChatForBot(message);
		PuObject data = PuObject.fromObject(new MapTuple<>("value", messageData));
		getLogger().debug("data from bot is {}", data.toJSON());
		message.setData(data);
		this.sendToUser(message, user);
	}

	private String getDataMessageChat(ChatMessage message){
		String mess = (String) message.getData().getString("value");
		com.google.common.reflect.TypeToken<List<NhoPoint>> token = new com.google.common.reflect.TypeToken<List<NhoPoint>>() {
			private static final long serialVersionUID = 1L;
		};
		Gson gSon = new Gson();
		List<NhoPoint> points = gSon.fromJson(mess, token.getType());
		String messageData = gSon.toJson(points);
		return messageData;
	}
	
	private String getDataChatForBot(ChatMessage message) {
		String mess = (String) message.getData().getString("value");
		com.google.common.reflect.TypeToken<List<NhoPoint>> token = new com.google.common.reflect.TypeToken<List<NhoPoint>>() {
			private static final long serialVersionUID = 1L;
		};
		Gson gSon = new Gson();
		List<NhoPoint> points = gSon.fromJson(mess, token.getType());
		List<NhoPoint> newPoints = new ArrayList<>();
		for (NhoPoint point : points) {
			NhoPoint newPoint = new NhoPoint(1 - point.getX(), 1 - point.getY(), point.getIndex(), point.getAction());
			newPoints.add(newPoint);
		}
		String messageData = gSon.toJson(newPoints);
		return messageData;
	}

	private void pushNotificationChat(NotificationHelper notiHelper, ChatMessage messageChat,
			Set<String> userInChannelOnlines) {
		String message = getMessagePushNotification(PushNotificationType.CHAT_MESSAGE, messageChat);
		for (String userName : userInChannelOnlines) {
			User user = this.getUserManager().getUserByUserName(userName);
			if (user != null) {
				for (String session : user.getSessions()) {
					if (!this.getUserManager().isDeviceInApp(session)) {
						String deviceToken = this.getUserManager().getDeviceTokenBySessionId(session);
						notiHelper.pushByGCM(message, userName, deviceToken, "chat");
					}
				}
			}
		}
	}

	private boolean isUserInChannelPushed(String userName, String channelId) {
		boolean isPushed = false;
		if (this.getUserManager().isUserInChannelPushed(userName, channelId)) {
			isPushed = true;
		}
		return isPushed;
	}

	private String getMessagePushNotification(PushNotificationType type, ChatMessage data) {
		PuObject message = new PuObject();
		message.setString(PNF.SENDER_NAME, data.getFrom());
		message.setInteger(PNF.PUSH_NOTIFICATION_TYPE, type.getCode());
		return message.toJSON();
	}
}
