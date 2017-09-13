package com.nho.server.processors.chat.anything;

import java.util.List;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuObject;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.DeleteLiveObjectMessage;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.LiveChatException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.DELETE_LIVE_OBJECT })
public class DeleteLiveObjectProcessor extends AbstractNhoProcessor<DeleteLiveObjectMessage> {

	@Override
	protected void process(DeleteLiveObjectMessage request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null || request.getChannelId() == null) {
				getLogger().debug("user not login ");
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			getLogger().debug("request to channel " + request.getChannelId());
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				getLogger().debug("delete obj in channel with bot");
				deleteLiveObj(request.getObjIds());
				return;
			}
			Set<String> userInChannelOnlines = helper.getOnlineUsersInChannel(request.getChannelId(),
					request.getFrom());
			if (userInChannelOnlines.size() > 0) {
				for (String userOnline : userInChannelOnlines) {
					boolean isReceiverBuzy = helper.isReceiverIsBusy(request.getChannelId(), request.getFrom());
					if (!isReceiverBuzy) {
						this.sendToUserName(request, userOnline);
						deleteLiveObj(request.getObjIds());
						for (String objectId : request.getObjIds()) {
							sendLogDeleteLiveObject(user.getUserName(), objectId,request.getSessionId());
						}
					} else {
						getLogger().debug("receiver is busy");
					}
				}
			} else {
				getLogger().debug("receiver is offline");
			}
		} catch (Exception exception) {
			throw new LiveChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	/**
	 * delete live objects send to
	 * {@link com.nho.chat.router.impl.DeleteLiveObjectProcessor}
	 */
	private void deleteLiveObj(List<String> objIds) {
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.DELETE_OBJ.getCode());
		PuArray array = new PuArrayList();
		for (String objId : objIds) {
			array.addFrom(objId);
		}
		data.setPuArray(ChatField.OBJ_IDS, array);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);

	}

	private void sendLogDeleteLiveObject(String userName, String liveObjectId,String sessionId) {
		String content = "user " + userName + " delete live object " + liveObjectId;
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.DELETE_LIVE_OBJECT, userName, sessionId);
	}
}
