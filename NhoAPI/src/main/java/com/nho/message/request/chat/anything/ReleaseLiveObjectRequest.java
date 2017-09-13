package com.nho.message.request.chat.anything;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class ReleaseLiveObjectRequest extends NhoMessage implements Request{
	{
		this.setType(MessageType.RELEASE_OBJ);
	}
	private String objId ;
	private String channelId ;
	private long timeStamp;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.objId);
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.timeStamp);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.objId = puArray.remove(0).getString();
		this.channelId = puArray.remove(0).getString();
		this.timeStamp = puArray.remove(0).getLong();
	}
	
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	
	
	public String getObjId() {
		return objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
	}
}
