package com.nho.server.processors.chat.anything;

import com.nhb.common.data.PuObject;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.CanMoveLiveObjectRequest;
import com.nho.message.response.chat.anything.CanMoveLiveObjectResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.LiveChatException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;

@NhoCommandProcessor(command = { MessageType.CAN_MOVE_DATA_REQUEST })
public class CanMoveLiveObjectProcessor extends AbstractNhoProcessor<CanMoveLiveObjectRequest> {

	@Override
	protected void process(CanMoveLiveObjectRequest request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("user not login");
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				CanMoveLiveObjectResponse response = new CanMoveLiveObjectResponse();
				response.setCanMove(true);
				response.setObjId(request.getObjId());
				getLogger().debug("send CanMoveLiveObjResponse to user {}", user.getUserName());
				this.sendToUser(response, user);
			}
			for (String partnerName : helper.getUsersInChannelByChannelId(request.getChannelId(), request.getFrom())) {
				CanMoveLiveObjectResponse response = new CanMoveLiveObjectResponse();
				response.setCanMove(isCanMoveLiveObject(request.getObjId(), partnerName));
				response.setObjId(request.getObjId());
				getLogger().debug("send CanMoveLiveObjResponse to user {}", user.getUserName());
				this.sendToUser(response, user);
			}
		} catch (Exception exception) {
			throw new LiveChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}
	/**
	 * check can move liveObject by user
	 * send command {@link ChannelCommand#CAN_MOVE_OBJECT} to {@link com.nho.chat.router.impl.CanMoveLiveObjectProcessor}
	 */
	private boolean isCanMoveLiveObject(String objectId, String partnerName) {
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.CAN_MOVE_OBJECT.getCode());
		data.setString(ChatField.OBJ_ID, objectId);
		data.setString(ChatField.USER_NAME, partnerName);
		boolean isCanMove = false;
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		int status = result.getInteger(ChatField.STATUS);
		if (status == 0) {
			isCanMove = result.getBoolean(ChatField.IS_BLOCK);
		}
		getLogger().debug("can move live object ?: " + isCanMove);
		return isCanMove;
	}
}
