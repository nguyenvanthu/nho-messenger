package com.nho.message.request.chat.anything;

import java.util.concurrent.atomic.AtomicInteger;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.Error;

public class MoveLiveObjectMessage extends NhoMessage implements Request{
	private static final AtomicInteger messageIdSeed = new AtomicInteger(0);
	{
		this.setType(MessageType.PICK_DATA_MESSAGE);
	}
	private String channelId ;
	private String objId ;
	private String from ;
	private PuObject data ;
	private long sentTime ;
	private Error error ;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.objId);
		puArray.addFrom(this.from);
		puArray.addFrom(this.data);
		puArray.addFrom(this.sentTime);
		puArray.addFrom(this.error == null ? null : this.error.getCode());
	}
	
	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelId = puArray.remove(0).getString();
		this.objId = puArray.remove(0).getString();
		this.from = puArray.remove(0).getString();
		this.data = puArray.remove(0).getPuObject();
		this.sentTime = puArray.remove(0).getLong();
		PuValue error = puArray.remove(0);
		if(error != null && error.getType() != PuDataType.NULL){
			this.error = Error.fromCode(error.getInteger());
		}
	}
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public PuObject getData() {
		return data;
	}
	public void setData(PuObject data) {
		this.data = data;
	}
	public long getSentTime() {
		return sentTime;
	}
	public void setSentTime(long sentTime) {
		this.sentTime = sentTime;
	}
	public void autoMessageId() {
		this.setMessageId(messageIdSeed.incrementAndGet());
	}
}
