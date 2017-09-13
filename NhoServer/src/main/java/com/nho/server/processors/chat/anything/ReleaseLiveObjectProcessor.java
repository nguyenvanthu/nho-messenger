package com.nho.server.processors.chat.anything;

import java.util.Set;

import com.nhb.common.data.PuObject;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.ReleaseLiveObjectRequest;
import com.nho.message.response.chat.anything.ReleaseObjResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.uams.statics.ActivityType;

/**
 * this is class process request release live object from clients this send to
 * Chat Server -> get position of object and send back to client
 */
@NhoCommandProcessor(command = { MessageType.RELEASE_OBJ })
public class ReleaseLiveObjectProcessor extends AbstractNhoProcessor<ReleaseLiveObjectRequest> {

	@Override
	protected void process(ReleaseLiveObjectRequest request) {
		ReleaseObjResponse response = new ReleaseObjResponse();
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null) {
			getLogger().debug("user not loggin");
			return;
		}
		ChannelHelper helper = new ChannelHelper(getContext());
		getLogger().debug("user {} release object {}", user.getUserName(), request.getObjId());
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.RELEASE_OBJECT.getCode());
		data.setString(ChatField.OBJ_ID, request.getObjId());
		data.setString(ChatField.USER_NAME, user.getUserName());
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		int status = result.getInteger(ChatField.STATUS);
		if (status == 0) {
			getLogger().debug("realse obj successful");
			float x = result.getFloat(ChatField.X);
			float y = result.getFloat(ChatField.Y);
			response.setUserName(user.getUserName());
			response.setX(x);
			response.setY(y);
			response.setTimeStamp(request.getTimeStamp());
			response.setObjId(request.getObjId());
			Set<String> subs = helper.getUsersInChannelByChannelId(request.getChannelId(), user.getUserName());
			this.sendToUserNames(response, subs);
			this.sendToUser(response, user);
			sendLogReleaseLiveObject(user.getUserName(), request.getObjId(),request.getSessionId());
		} else {
			getLogger().debug("error when free obj or object is blocked by partner");
		}
	}

	private void sendLogReleaseLiveObject(String userName, String liveObjectId,String sessionId) {
		String content = "user " + userName + " release live object " + liveObjectId;
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.RELEASE_LIVE_OBJECT, userName, sessionId);
	}
}
