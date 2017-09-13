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

@ChatCommandProcessor(command = { ChannelCommand.STORE_DATA_MSG })
public class StoreDataMsgProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		data.setType(ChatField.DATA_MESSAGE, PuDataType.STRING);
		String userName = data.getString(ChatField.USER_NAME);
		String dataMsg = data.getString(ChatField.DATA_MESSAGE);
		this.getChatManager().addMessageOfUser(userName, dataMsg);
		return PuObject.fromObject(new MapTuple<>(ChatField.STATUS, 0));
	}

}
