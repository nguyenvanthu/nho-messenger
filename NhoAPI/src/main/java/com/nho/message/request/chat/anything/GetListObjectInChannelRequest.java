package com.nho.message.request.chat.anything;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class GetListObjectInChannelRequest extends NhoMessage implements Request{
	{
		this.setType(MessageType.GET_LIST_OBJECT_IN_CHANNEL);
	}
	private String channelId ;
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.channelId);
	}
	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelId = puArray.remove(0).getString();
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
}
