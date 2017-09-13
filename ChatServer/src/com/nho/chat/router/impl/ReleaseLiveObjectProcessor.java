package com.nho.chat.router.impl;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.entity.BasicLiveObjectInfo;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.RELEASE_OBJECT })
public class ReleaseLiveObjectProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.OBJ_ID, PuDataType.STRING);
		String objId = data.getString(ChatField.OBJ_ID);
		String userName = data.getString(ChatField.USER_NAME);
		this.getChatManager().removeBlockedObjByUserName(objId, userName);
		if (this.getChatManager().isObjectIsBlocked(objId)) {
			// object is blocked -> don't sent latest position of object
			getLogger().debug("don't sent postion of live object");
			PuObject response = new PuObject();
			response.setInteger(ChatField.STATUS, 1);
			return response;
		} else {
			getLogger().debug("get position of live object");
			// get position of object
			try {
				BasicLiveObjectInfo stroke = this.getChatManager().getStrokeOfObj(objId);
				float x = stroke.getPostion().getX();
				float y = stroke.getPostion().getY();

				PuObject response = new PuObject();
				response.setInteger(ChatField.STATUS, 0);
				response.setFloat(ChatField.X, x);
				response.setFloat(ChatField.Y, y);
				return response;
			} catch (Exception exception) {
				PuObject response = new PuObject();
				response.setInteger(ChatField.STATUS, 1);
				return response;
			}
		}
	}
}
