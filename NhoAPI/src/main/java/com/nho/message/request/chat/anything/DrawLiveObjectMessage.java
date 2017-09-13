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

public class DrawLiveObjectMessage extends NhoMessage implements Request {
	{
		this.setType(MessageType.DRAW_LIVE_OBJECT);
	}
	private static AtomicInteger seed;

	private static int genMessageId() {
		if (seed == null) {
			seed = new AtomicInteger(0);
		}
		return seed.getAndIncrement();
	}

	private int id = genMessageId();
	private String from;
	private String to;
	private long sentTime;
	private PuObject data;
	private String objId;
	private String dataType = "";
	private boolean isEnd = false;

	private Error error;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.id);
		puArray.addFrom(this.from);
		puArray.addFrom(this.to);
		puArray.addFrom(this.sentTime);
		puArray.addFrom(this.data);
		puArray.addFrom(this.objId);
		puArray.addFrom(this.dataType);
		puArray.addFrom(this.isEnd);
		puArray.addFrom(this.error == null ? null : this.error.getCode());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.id = puArray.remove(0).getInteger();
		this.from = puArray.remove(0).getString();
		this.to = puArray.remove(0).getString();
		this.sentTime = puArray.remove(0).getLong();
		this.data = puArray.remove(0).getPuObject();
		this.objId = puArray.remove(0).getString();
		this.dataType = puArray.remove(0).getString();
		this.isEnd = puArray.remove(0).getBoolean();
		PuValue error = puArray.remove(0);
		if (error != null && error.getType() != PuDataType.NULL) {
			this.error = Error.fromCode(error.getInteger());
		}
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public long getSentTime() {
		return sentTime;
	}

	public void setSentTime(long sentTime) {
		this.sentTime = sentTime;
	}

	public PuObject getData() {
		return data;
	}

	public void setData(PuObject data) {
		this.data = data;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public int getId() {
		return this.id;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

}
