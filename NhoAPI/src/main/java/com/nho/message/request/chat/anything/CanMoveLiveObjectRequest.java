package com.nho.message.request.chat.anything;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class CanMoveLiveObjectRequest extends NhoMessage implements Request{
	{
		this.setType(MessageType.CAN_MOVE_DATA_REQUEST);
	}
	private String channelId ;
	private String from ;
	private String objId ;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.from);
		puArray.addFrom(this.objId);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelId = puArray.remove(0).getString();
		this.from = puArray.remove(0).getString();
		this.objId = puArray.remove(0).getString();
	}
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getObjId() {
		return objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
	}
	
	
}
