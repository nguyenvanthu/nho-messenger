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

@ChatCommandProcessor(command = { ChannelCommand.GET_TIME_CHAT_WITH_BOT })
public class GetTimeChatWithBotProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		String userName = data.getString(ChatField.USER_NAME);
		long timeChat = this.getChatManager().getTimeChatOfUser(userName);
		PuObject result = new PuObject();
		result.set(ChatField.STATUS, 0);
		result.set(ChatField.TIME, timeChat);
		return result;
	}
}
