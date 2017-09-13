package com.nho.chat.router.impl;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.data.ChannelMongoBean;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.statics.Error;
/**
 * get users in channel by channelId 
 */
@ChatCommandProcessor(command = { ChannelCommand.GET_CHANNEL })
public class GetChannelProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		ChannelMongoBean channel = getChannel(channelId);
		if (channel == null) {
			getLogger().debug("channel null");
			response.setInteger(ChatField.STATUS, 1);
			response.setInteger(ChatField.ERROR, Error.CHANNEL_NOT_FOUND.getCode());
			return response;
		} else {
			PuArray subcriber = new PuArrayList();
			for (String sub : this.getChannelManager().getUserInChannels(channel)) {
				subcriber.addFrom(sub);
			}
			response.setInteger(ChatField.STATUS, 0);
			response.setPuArray(ChatField.SUBCRIBE, subcriber);
			return response;
		}
	}

	public ChannelMongoBean getChannel(String channelId) {
		ChannelMongoBean channel = null;
		channel = this.getChannelMongoModel().findChannelById(channelId);
		return channel;
	}

}
