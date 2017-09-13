package com.nho.message.request.chat;

import java.util.concurrent.atomic.AtomicInteger;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.Error;

/**
 * message chat : A sent to B from : userName of A to : userName of B data :
 * content message sentTime
 * 
 * @author trial
 *
 */
public class ChatMessage extends NhoMessage implements Request {

	private static final AtomicInteger messageIdSeed = new AtomicInteger(0);

	{
		this.setType(MessageType.CHAT);
	}

	private String from;
	private String to;
	private long sentTime;
	private PuObject data;
	private int stickerType;

	private Error error;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.from);
		puArray.addFrom(this.to);
		puArray.addFrom(this.sentTime);
		puArray.addFrom(this.data);
		puArray.addFrom(this.stickerType);
		puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.from = puArray.remove(0).getString();
		this.to = puArray.remove(0).getString();
		this.sentTime = puArray.remove(0).getLong();
		this.data = puArray.remove(0).getPuObject();
		this.stickerType = puArray.remove(0).getInteger();
		PuValue error = puArray.remove(0);
		if (error != null && error.getType() != PuDataType.NULL) {
			this.setError(Error.fromCode(error.getInteger()));
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

	public void setTo(String channel) {
		this.to = channel;
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

	public void autoSentTime() {
		this.setSentTime(System.nanoTime());
	}

	public void autoMessageId() {
		this.setMessageId(messageIdSeed.incrementAndGet());
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public int getStickerType() {
		return stickerType;
	}

	public void setStickerType(int stickerType) {
		this.stickerType = stickerType;
	}

}
