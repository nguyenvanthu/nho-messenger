package com.nho.server.processors.chat.anything;

import java.util.Set;

import com.nhb.common.data.PuObject;
import com.nho.chat.router.impl.AddLiveObjectProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.MakeLiveObject;
import com.nho.message.response.chat.anything.MakeObjectCompleteResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.chat.LiveObject;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.MAKE_OBJECT_CHAT })
public class MakeLiveObjectProcessor extends AbstractNhoProcessor<MakeLiveObject> {

	@Override
	protected void process(MakeLiveObject request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null || request.getChannelId() == null) {
				getLogger().debug("user not login");
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			getLogger().debug("request to channel " + request.getChannelId());
			MakeObjectCompleteResponse makeObjectResponse = new MakeObjectCompleteResponse();
			makeObjectResponse.setChannelId(request.getChannelId());
			makeObjectResponse.setFrom(user.getUserName());
			makeObjectResponse.setStartId(request.getStartId());
			makeObjectResponse.setEndId(request.getEndId());
			makeObjectResponse.setObjId(request.getObjId());
			makeObjectResponse.setDataType("");
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				getLogger().debug("make obj with bot");
				// storeLiveObject(request, user.getUserName(),
				// request.getObjId(), request.getChannelId());
				makeObjectResponse.setData("");
				this.sendToUser(makeObjectResponse, user);
				return;
			}
			String dataLiveObject = storeLiveObject(request, user.getUserName(), request.getObjId(),
					request.getChannelId());
			if (dataLiveObject != null) {
				makeObjectResponse.setData(dataLiveObject);
			}
			Set<String> subs = helper.getUsersInChannelByChannelId(request.getChannelId(), request.getFrom());
			getLogger().debug("send to number users: " + subs.size());
			this.sendToUserNames(makeObjectResponse, subs);
			this.sendToUser(makeObjectResponse, user);
			resetIndex(user.getUserName(), request.getChannelId());
			logMakeLiveObject(user.getUserName(), subs.iterator().next(), request.getObjId(),request.getSessionId());
		} catch (Exception exception) {
			getLogger().debug(exception.toString());
		}
	}

	private void resetIndex(String userName, String channelId) {
		String userInChannel = userName + channelId;
		LiveObject.ressetIndex(userInChannel);
	}

	/**
	 * store live object send command to {@link AddLiveObjectProcessor}
	 */
	private String storeLiveObject(MakeLiveObject request, String userName, String objId, String channelId) {
		String dataLiveObject = null;
		getLogger().debug("send command store live object to Chat Server");
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.ADD_LIVE_OBJECT.getCode());
		data.setString(ChatField.OBJ_ID, objId);
		data.setString(ChatField.SENDER_NAME, userName);
		data.setInteger(ChatField.START_ID, request.getStartId());
		data.setInteger(ChatField.END_ID, request.getEndId());
		data.setString(ChatField.CHANNEL_ID, channelId);
		data.setFloat(ChatField.X, request.getX());
		data.setFloat(ChatField.Y, request.getY());
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		if (result.getInteger(ChatField.STATUS) == 0) {
			dataLiveObject = result.getString(ChatField.LIVE_OBJ_DATA);
		}
		return dataLiveObject;
	}

	private void logMakeLiveObject(String userName, String friend, String liveObjectId,String sessionId) {
		String content = "user " + userName + " make live object " + liveObjectId + " when chat with " + friend;
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.MAKE_LIVE_OBJECT, userName, sessionId);
	}
}
