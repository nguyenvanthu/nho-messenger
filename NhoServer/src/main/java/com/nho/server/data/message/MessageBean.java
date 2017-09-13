package com.nho.server.data.message;

import java.util.UUID;

import com.nhb.common.db.beans.UUIDBean;
import com.nhb.common.utils.Converter;

public class MessageBean extends UUIDBean {

	private static final long serialVersionUID = 1L;

	private byte[] content;
	private int timestamp;
	private byte[] fromUserId;
	private byte[] toChannelId;

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public byte[] getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(byte[] fromUserId) {
		this.fromUserId = fromUserId;
	}

	public byte[] getToChannelId() {
		return toChannelId;
	}

	public void setToChannelId(byte[] toChannelId) {
		this.toChannelId = toChannelId;
	}

	public UUID getFromUserUUID() {
		return Converter.bytesToUUID(this.getFromUserId());
	}

	public UUID getToChannelUUID() {
		return Converter.bytesToUUID(this.toChannelId);
	}
}
