package com.nho.message.request.channel;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class SoundPermissionMessage extends NhoMessage implements Request{
	{
		this.setType(MessageType.SOUND_PERMISSION);
	}
	private String from ;
	private String channelId ;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.from);
		puArray.addFrom(this.channelId);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.from = puArray.remove(0).getString();
		this.channelId = puArray.remove(0).getString();
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
}
