package com.nho.message.request.channel;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class LeaveChannelRequest extends NhoMessage implements Request {

	{
		this.setType(MessageType.LEAVE_CHANNEL);
	}

	private String channelId;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.channelId);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelId = puArray.remove(0).getString();
	}
}
