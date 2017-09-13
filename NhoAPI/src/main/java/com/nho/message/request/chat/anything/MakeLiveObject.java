package com.nho.message.request.chat.anything;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class MakeLiveObject extends NhoMessage implements Request{
	{
		this.setType(MessageType.MAKE_OBJECT_CHAT);
	}
	private String channelId ;
	private String from ;
	private int startId ;
	private int endId ;
	private float x ;
	private float y;
	private String objId ;
	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.from);
		puArray.addFrom(this.startId);
		puArray.addFrom(this.endId);
		puArray.addFrom(this.x);
		puArray.addFrom(this.y);
		puArray.addFrom(this.objId);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelId = puArray.remove(0).getString();
		this.from = puArray.remove(0).getString();	
		this.startId = puArray.remove(0).getInteger();
		this.endId = puArray.remove(0).getInteger();
		this.x = puArray.remove(0).getFloat();
		this.y = puArray.remove(0).getFloat();
		this.objId = puArray.remove(0).getString();
	}
	
	public int getStartId() {
		return startId;
	}

	public void setStartId(int startId) {
		this.startId = startId;
	}

	public int getEndId() {
		return endId;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setEndId(int endId) {
		this.endId = endId;
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
}
