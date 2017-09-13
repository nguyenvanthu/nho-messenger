package com.nho.chat.router.impl;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.UPDATE_STATUS })
public class UpdateStatusLiveObjectProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.OBJ_ID,PuDataType.STRING);
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		String objId = data.getString(ChatField.OBJ_ID);
		String userName = data.getString(ChatField.USER_NAME);
		this.getChatManager().addNewBlockedObj(objId, userName);
		return null;
	}

}
