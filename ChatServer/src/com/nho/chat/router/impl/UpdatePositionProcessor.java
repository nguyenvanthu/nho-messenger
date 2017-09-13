package com.nho.chat.router.impl;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.UPDATE_POSITION })
public class UpdatePositionProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.OBJ_ID, PuDataType.STRING);
		String objId = data.getString(ChatField.OBJ_ID);
		float x = data.getFloat(ChatField.X);
		float y = data.getFloat(ChatField.Y);
		this.getChatManager().updatePositionOfObj(objId, x, y);
		return PuObject.fromObject(new MapTuple<>(ChatField.STATUS, 0));
	}

}
