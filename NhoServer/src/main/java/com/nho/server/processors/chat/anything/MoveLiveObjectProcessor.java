package com.nho.server.processors.chat.anything;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuObject;
import com.nho.chat.router.impl.UpdateStatusLiveObjectProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.MoveLiveObjectMessage;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.LiveChatException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;

@NhoCommandProcessor(command = { MessageType.PICK_DATA_MESSAGE })
public class MoveLiveObjectProcessor extends AbstractNhoProcessor<MoveLiveObjectMessage> {
	private static Map<Integer, MoveLiveObjectMessage> idMessages = new ConcurrentHashMap<>();
	private static Map<String, Integer> expectedIdOfSessions = new ConcurrentHashMap<>();

	private int getExpectedMessageId(String sessionId) {
		if (expectedIdOfSessions.containsKey(sessionId)) {
			return expectedIdOfSessions.get(sessionId);
		} else {
			expectedIdOfSessions.put(sessionId, 1);
			return 1;
		}
	}

	private List<MoveLiveObjectMessage> getMessageToSent(int expectedId) {
		List<MoveLiveObjectMessage> messageToSents = new ArrayList<>();
		if (idMessages.containsKey(expectedId)) {
			messageToSents.add(idMessages.get(expectedId));
		} else {
			Set<Integer> ids = idMessages.keySet();
			for (int id : ids) {
				if (id < expectedId) {
					messageToSents.add(idMessages.get(id));
				}
			}
		}
		return messageToSents;
	}

	@Override
	protected void process(MoveLiveObjectMessage request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null || request.getChannelId() == null) {
				getLogger().debug("user not login in PickDataMove ");
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			String sessionId = request.getSessionId();
			if (expectedIdOfSessions.containsKey(sessionId)) {
				expectedIdOfSessions.put(sessionId, expectedIdOfSessions.get(sessionId) + 1);
			}
			int expectedMessageId = getExpectedMessageId(sessionId);
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				// boot do nothing
				this.updateLiveObjPosition(request);
				resetWaitTimeOfUser(user.getUserName());
				return;
			}
			Set<String> userInChannelOnlines = helper.getOnlineUsersInChannel(request.getChannelId(),
					request.getFrom());
			if (userInChannelOnlines.size() > 0) {
				for (String userOnline : userInChannelOnlines) {
					boolean isReceiverBuzy = helper.isReceiverIsBusy(request.getChannelId(), request.getFrom());
					if (!isReceiverBuzy) {
						if (request.getMessageId() > expectedMessageId) {
							idMessages.put(request.getMessageId(), request);
							List<MoveLiveObjectMessage> messageToSents = getMessageToSent(expectedMessageId);
							if (messageToSents.size() > 0) {
								for (MoveLiveObjectMessage message : messageToSents) {
									getLogger().debug("send to user "+userOnline);
									this.sendToUserName(message, userOnline);
								}
							}
						} else {
							getLogger().debug("send to user "+userOnline);
							this.sendToUserName(request, userOnline);
						}
						this.updateLiveObjPosition(request);
						this.updateStatusLiveObj(request.getObjId(), user.getUserName());
					} else {
						getLogger().debug("cannot send chat message because reciver is buzy");
						request.setError(Error.RECEIVER_BUZY);
						this.send(request, user.getSessions());
					}
				}
			} else {
				getLogger().debug("error user receiver chat message offline ");
				request.setError(Error.USER_OFFLINE);
				this.send(request, user.getSessions());
			}
		} catch (Exception exception) {
			throw new LiveChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}

	}

	private void updateLiveObjPosition(MoveLiveObjectMessage request) {
		getLogger().debug("update position live obj to server");
		try {
			String objId = request.getObjId();
			String mess = (String) request.getData().getString("value");
			mess = mess.replace('[', ' ');
			mess = mess.replace(']', ' ');
			String[] values = mess.split(",");
			getLogger().debug(mess);
			float x = Float.parseFloat(values[0]);
			float y = Float.parseFloat(values[1]);
			PuObject data = new PuObject();
			data.setInteger(ChatField.COMMAND, ChannelCommand.UPDATE_POSITION.getCode());
			data.setString(ChatField.OBJ_ID, objId);
			data.setFloat(ChatField.X, x);
			data.setFloat(ChatField.Y, y);
			this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		} catch (Exception exception) {
			getLogger().debug("error when parse position live obj: " + exception);
		}
	}

	/**
	 * update status of live object 
	 * send command to {@link UpdateStatusLiveObjectProcessor}
	 */
	private void updateStatusLiveObj(String objId, String userName) {
		getLogger().debug("update status live obj to server");
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.UPDATE_STATUS.getCode());
		data.setString(ChatField.OBJ_ID, objId);
		data.setString(ChatField.USER_NAME, userName);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
	}

	private void resetWaitTimeOfUser(String userName) {
		getLogger().debug("reset wait time of user " + userName);
		PuObject resetWaiter = new PuObject();
		resetWaiter.setInteger(ChatField.COMMAND, ChannelCommand.RESET_WAIT_TIME.getCode());
		resetWaiter.setString(ChatField.USER_NAME, userName);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, resetWaiter);
	}
}
