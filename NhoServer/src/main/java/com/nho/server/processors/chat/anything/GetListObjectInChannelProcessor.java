package com.nho.server.processors.chat.anything;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.chat.router.impl.GetObjectsInChannelProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.GetListObjectInChannelRequest;
import com.nho.message.response.chat.anything.GetLiveObjectsInChannelResponse;
import com.nho.message.response.chat.anything.ListObjectInChannelResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;

/**
 * get live objects in channel send command to
 * {@link GetObjectsInChannelProcessor}
 */
@NhoCommandProcessor(command = { MessageType.GET_LIST_OBJECT_IN_CHANNEL })
public class GetListObjectInChannelProcessor extends AbstractNhoProcessor<GetListObjectInChannelRequest> {

	@Override
	protected void process(GetListObjectInChannelRequest request) {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null || request.getChannelId() == null) {
			getLogger().debug("user not login ");
			ListObjectInChannelResponse response = new ListObjectInChannelResponse();
			response.setSuccessful(false);
			response.setError(Error.USER_NOT_LOGGED_IN);
			this.send(response, request.getSessionId());
			return;
		}

		getLogger().debug("get list live obj in channel " + request.getChannelId());
		ChannelHelper helper = new ChannelHelper(getContext());
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.GET_LIST_OBJ_CHANNEL.getCode());
		data.setString(ChatField.CHANNEL_ID, request.getChannelId());
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		if (result == null) {
			getLogger().debug("response from chat server null");
			return;
		}
		Set<String> subcribes = helper.getUserInChannel(request.getChannelId());
		int status = result.getInteger(ChatField.STATUS);
		if (status == 0) {
			getLogger().debug("get response from chat server");
			GetLiveObjectsInChannelResponse response = new GetLiveObjectsInChannelResponse();
			List<String> liveObjects = new ArrayList<>();
			PuArray liveObjectArray = result.getPuArray(ChatField.LIVE_OBJS);
			for (PuValue value : liveObjectArray) {
				liveObjects.add(value.getString());
			}
			getLogger().debug("number liveObject: "+liveObjects.size());
			response.setLiveObjects(liveObjects);
			response.setSuccessful(true);
//			this.sendToUser(response, user);
			this.sendToUserNames(response, subcribes);
		} else {
			getLogger().debug("something wrong in Chat Server ");
			ListObjectInChannelResponse response = new ListObjectInChannelResponse();
			response.setSuccessful(false);
			this.send(response, request.getSessionId());
			return;
		}
	}
}
