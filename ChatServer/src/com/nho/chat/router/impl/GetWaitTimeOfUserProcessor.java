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

@ChatCommandProcessor(command = { ChannelCommand.GET_WAIT_TIME })
public class GetWaitTimeOfUserProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		String userName = data.getString(ChatField.USER_NAME);
		long waitTime = this.getChatManager().getWaitTimeOfUser(userName);
//		getLogger().debug("wait time is " + waitTime);
		PuObject result = new PuObject();
		result.setInteger(ChatField.STATUS, 0);
		result.setLong(ChatField.WAIT_TIME, waitTime);
		return result;
	}

}
