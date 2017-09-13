package com.nho.chat.router.impl;

import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
/**
 *  return channelIds by userName
 */
@ChatCommandProcessor(command = { ChannelCommand.GET_CHANNEL_BY_USER })
public class GetChannelByUserProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		String userName = data.getString(ChatField.USER_NAME);
		List<String> channels = this.getChannelManager().getChannelIdByUserName(userName);
		PuArray array = new PuArrayList();
		for (String channel : channels) {
			array.addFrom(channel);
		}
		PuObject result = new PuObject();
		result.setInteger(ChatField.STATUS, 0);
		result.setPuArray(ChatField.CHANNELS, array);
		return result;
	}

}
