package com.nho.server.processors.chat.anything;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.chat.router.impl.AddStrokeProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.DrawLiveObjectMessage;
import com.nho.message.response.chat.anything.MakeObjectCompleteResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.chat.LiveObject;
import com.nho.server.entity.user.User;
import com.nho.server.exception.UserNotLoggedInException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.NotificationHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.ChatCounter;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;
import com.nho.statics.PNF;
import com.nho.statics.PushNotificationType;

@NhoCommandProcessor(command = { MessageType.DRAW_LIVE_OBJECT })
public class DrawLiveObjectMessageProcessor extends AbstractNhoProcessor<DrawLiveObjectMessage> {
	private static Map<Integer, DrawLiveObjectMessage> idToMessages = new ConcurrentHashMap<>();
	private static Map<String, Integer> sessionToExpectedIds = new ConcurrentHashMap<>();

	private int getExpectedIdOfMessage(String sessionId) {
		synchronized (sessionToExpectedIds) {
			return sessionToExpectedIds.get(sessionId);
		}
	}

	private void incrementExpectedIdOfSession(String sessionId) {
		synchronized (sessionToExpectedIds) {
			if (sessionToExpectedIds.containsKey(sessionId)) {
				sessionToExpectedIds.put(sessionId, sessionToExpectedIds.get(sessionId) + 1);
			} else {
				sessionToExpectedIds.put(sessionId, 1);
			}
		}
	}

	private List<DrawLiveObjectMessage> getMessageExpected(int expectedMsgId) {
		List<DrawLiveObjectMessage> messageToSents = new ArrayList<>();
		synchronized (idToMessages) {
			if (idToMessages.containsKey(expectedMsgId)) {
				messageToSents.add(idToMessages.get(expectedMsgId));
				idToMessages.remove(expectedMsgId);
			} else {
				for (int id : idToMessages.keySet()) {
					if (id < expectedMsgId) {
						messageToSents.add(idToMessages.get(id));
						idToMessages.remove(id);
					}
				}
			}

			return messageToSents;
		}
	}

	@Override
	protected void process(DrawLiveObjectMessage request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null || request.getTo() == null) {
				getLogger().debug("user not login");
				throw new UserNotLoggedInException();
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			NotificationHelper notiHelper = new NotificationHelper(getContext());
			String sessionId = request.getSessionId();
			incrementExpectedIdOfSession(sessionId);
			int expectedMessageId = getExpectedIdOfMessage(sessionId);
			getLogger().debug("request to channel " + request.getTo());
			if (helper.isChannelChatWithBot(request.getTo())) {
				getLogger().debug("user {} draw live object with bot ", user.getUserName());
				// chatWithBot(request, user.getUserName());
				return;
			}
			getLogger().debug("objId is " + request.getObjId());
			Set<String> userInChannelOnlines = helper.getOnlineUsersInChannel(request.getTo(), request.getFrom());
			if (userInChannelOnlines.size() > 0) {
				for (String userOnline : userInChannelOnlines) {
					boolean isReceiverBuzy = helper.isReceiverIsBusy(request.getTo(), request.getFrom());
					if (isReceiverBuzy) {
						getLogger().debug("cannot send chat message because reciver is buzy");
						request.setError(Error.RECEIVER_BUZY);
						this.send(request, user.getSessions());

						List<String> userBusys = helper.getUserBusysInChannel(request.getTo(), request.getFrom());
						pushNotificationToBusyUser(notiHelper, userBusys, request, user.getUserName());
						return;
					}
					addStrokeOfObjectId(helper, request, user);
					if (request.getMessageId() > expectedMessageId) {
						// message den khong dung thu tu
						idToMessages.put(request.getMessageId(), request);
						List<DrawLiveObjectMessage> messageToSents = getMessageExpected(expectedMessageId);
						if (messageToSents.size() > 0) {
							for (DrawLiveObjectMessage message : messageToSents) {
								String userInChannel = user.getUserName() + request.getTo();
								int index = LiveObject.getAndIncrementIndex(userInChannel);
								message.setMessageId(index);
								this.sendToUserName(message, userOnline);
							}
						}
					} else {
						this.sendToUserName(request, userOnline);
					}
				}
			} else {
				getLogger().debug("error user receiver chat message offline ");
				request.setError(Error.USER_OFFLINE);
				this.send(request, user.getSessions());
				sendPushNotificationToOfflineUser(helper, notiHelper, request, user.getUserName());
				return;
			}
		} catch (Exception exception) {
			getLogger().debug(exception.toString());
		}

	}

	private void sendPushNotificationToOfflineUser(ChannelHelper helper, NotificationHelper notiHelper,
			DrawLiveObjectMessage request, String userName) {
		String message = getMessagePushNotification(PushNotificationType.CHAT_MESSAGE, request);
		if (ChatCounter.isPushNotificationDraw(userName)) {
			getLogger().debug("send Push Notification to user offlines ");
			List<String> userOfflines = helper.getUserOfflinesInChannel(request.getTo(), request.getFrom());
			for (String userOffline : userOfflines) {
				if (!isUserInChannelPushed(userOffline, request.getTo())) {
					User offline = this.getUserManager().getUserByUserName(userOffline);
					if (offline != null) {
						if (offline.getSessions().size() > 0) {
							for (String session : offline.getSessions()) {
								String deviceToken = this.getUserManager().getDeviceTokenBySessionId(session);
								notiHelper.pushByGCM(message, userOffline, deviceToken, "drawLiveObj");
							}
						} else {
							notiHelper.pushByGCM(message, userOffline, "", "drawLiveObj");
						}
					}
					this.getUserManager().addUserInChannelPushed(userOffline, request.getTo());
				}
			}
		}
	}

	private void pushNotificationToBusyUser(NotificationHelper notiHelper, List<String> userBusys,
			DrawLiveObjectMessage request, String userName) {
		String message = getMessagePushNotification(PushNotificationType.CHAT_MESSAGE, request);
		if (ChatCounter.isPushNotificationDraw(userName)) {
			for (String userBusy : userBusys) {
				User busyUser = this.getUserManager().getUserByUserName(userBusy);
				if (busyUser.getSessions().size() > 0) {
					for (String session : busyUser.getSessions()) {
						if (this.getUserManager().isDeviceInApp(session)) {
							getLogger().debug("send push api chat to busy user");
							notiHelper.pushInApp(message, "chat", busyUser, PushNotificationType.CHAT_MESSAGE);
						} else {
							String deviceToken = this.getUserManager().getDeviceTokenBySessionId(session);
							notiHelper.pushByGCM(message, userBusy, deviceToken, "drawLiveObj");
						}
					}
				} else {
					notiHelper.pushByGCM(message, userBusy, "", "drawLiveObj");
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

	private String getMessagePushNotification(PushNotificationType type, DrawLiveObjectMessage data) {
		PuObject message = new PuObject();
		message.setString(PNF.SENDER_NAME, data.getFrom());
		message.setInteger(PNF.PUSH_NOTIFICATION_TYPE, type.getCode());

		return message.toJSON();
	}

	/**
	 * add stroke of liveObject send to {@link AddStrokeProcessor}
	 */
	private void addStrokeOfObjectId(ChannelHelper helper, DrawLiveObjectMessage request, User user) {
		getLogger().debug("add new stroke of objectId {}", request.getObjId());
		getLogger().debug("data of stroke: " + request.getData().getString("value"));
		PuObject addStrokeData = new PuObject();
		addStrokeData.setInteger(ChatField.COMMAND, ChannelCommand.ADD_STROKE.getCode());
		addStrokeData.setString(ChatField.DATA, request.getData().getString("value"));
		addStrokeData.setInteger(ChatField.ID, request.getId());
		addStrokeData.setString(ChatField.OBJ_ID, request.getObjId());
		addStrokeData.setString(ChatField.CHANNEL_ID, request.getFrom());
		addStrokeData.setString(ChatField.DATA_TYPE, request.getDataType());
		addStrokeData.setBoolean(ChatField.IS_END, request.isEnd());
		addStrokeData.setString(ChatField.SENDER_NAME, request.getFrom());
		addStrokeData.setString(ChatField.CHANNEL_ID, request.getTo());
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, addStrokeData);
		if (result.getInteger(ChatField.STATUS) == 2) {
			// send make object response when client draw game data
			result.setType(ChatField.LIVE_OBJ_DATA, PuDataType.STRING);
			int startId = result.getInteger(ChatField.START_ID);
			int endId = result.getInteger(ChatField.END_ID);
			String dataLiveObject = result.getString(ChatField.LIVE_OBJ_DATA);
			MakeObjectCompleteResponse makeObjectResponse = new MakeObjectCompleteResponse();
			makeObjectResponse.setChannelId(request.getTo());
			makeObjectResponse.setFrom(user.getUserName());
			makeObjectResponse.setStartId(startId);
			makeObjectResponse.setEndId(endId);
			makeObjectResponse.setObjId(request.getObjId());
			makeObjectResponse.setData(dataLiveObject);
			makeObjectResponse.setDataType(request.getDataType());

			Set<String> subs = helper.getUsersInChannelByChannelId(request.getTo(), request.getFrom());
			getLogger().debug("send to number users: " + subs.size());
			this.sendToUserNames(makeObjectResponse, subs);
			this.sendToUser(makeObjectResponse, user);
			resetIndex(user.getUserName(), request.getTo());
		}
	}

	private void resetIndex(String userName, String channelId) {
		String userInChannel = userName + channelId;
		LiveObject.ressetIndex(userInChannel);
	}
}
