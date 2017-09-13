package com.nho.message.request.chat;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class StickerChatMessage extends NhoMessage implements Request {
	{
		this.setType(MessageType.STICKER_CHAT);
	}
	private String stickerType;
	private String channelId;
	private PuObject data;
	private long sentTime;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public PuObject getData() {
		return data;
	}

	public void setData(PuObject data) {
		this.data = data;
	}

	public String getStickerType() {
		return stickerType;
	}

	public void setStickerType(String stickerType) {
		this.stickerType = stickerType;
	}

	public long getSentTime() {
		return sentTime;
	}

	public void setSentTime(long sentTime) {
		this.sentTime = sentTime;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.sentTime);
		puArray.addFrom(this.data);
		puArray.addFrom(this.stickerType);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelId = puArray.remove(0).getString();
		this.sentTime = puArray.remove(0).getLong();
		this.data = puArray.remove(0).getPuObject();
		this.stickerType = puArray.remove(0).getString();
	}
}
