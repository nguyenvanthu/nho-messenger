package com.nho.chat.router.impl;

import java.util.List;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.CHECK_CHANNEL })
public class CheckChannelProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		PuObject result = new PuObject();
		result.setInteger(ChatField.STATUS,0);
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		List<String> usersInChannel = this.getChannelManager().getUsersInSideChannel(channelId);
		if(usersInChannel.size()>0){
			result.setBoolean(ChatField.IS_EMPTY, false);
		}else {
			result.setBoolean(ChatField.IS_EMPTY, true);
		}
		return result;
	}

}
