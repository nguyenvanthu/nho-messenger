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

@ChatCommandProcessor(command = { ChannelCommand.UPDATE_PING_TIMES })
public class UpdatePingTimesProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		String userName = data.getString(ChatField.USER_NAME);
		int pingTimes = this.getChatManager().updatePingTimes(channelId, userName);
		PuObject result = new PuObject();
		result.setInteger(ChatField.STATUS, 0);
		result.setInteger(ChatField.PING_TIMES, pingTimes);
		return result;
	}

}
