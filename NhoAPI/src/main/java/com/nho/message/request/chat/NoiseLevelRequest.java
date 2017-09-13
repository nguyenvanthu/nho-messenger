package com.nho.message.request.chat;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class NoiseLevelRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.NOISE_LEVEL);
	}
	private int noise;
	private String channelId ;
	
	public int getNoise() {
		return noise;
	}
	public void setNoise(int noise) {
		this.noise = noise;
	} 
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.noise);
		puArray.addFrom(this.channelId);
	}
	@Override
	protected void readPuArray(PuArray puArray) {
		this.noise = puArray.remove(0).getInteger();
		this.channelId = puArray.remove(0).getString();
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
}
