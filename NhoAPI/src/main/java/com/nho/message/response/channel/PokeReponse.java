package com.nho.message.response.channel;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class PokeReponse extends NhoMessage {
	{
		this.setType(MessageType.POKE_RESPONSE);
	}
	private boolean success;
	private String senderName;
	private String senderDisplayName;
	private int pingTimes = 0;
	private int friendPingTimes = 0;

	public String getSenderDisplayName() {
		return senderDisplayName;
	}

	public void setSenderDisplayName(String senderDisplayName) {
		this.senderDisplayName = senderDisplayName;
	}

	private String channelId;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	private Error error;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.success);
		if (this.success) {
			puArray.addFrom(this.senderName);
			puArray.addFrom(this.senderDisplayName);
			puArray.addFrom(this.channelId);
			puArray.addFrom(this.pingTimes);
			puArray.addFrom(this.friendPingTimes);
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
		if (this.success) {
			this.senderName = puArray.remove(0).getString();
			this.senderDisplayName = puArray.remove(0).getString();
			this.channelId = puArray.remove(0).getString();
			this.pingTimes = puArray.remove(0).getInteger();
			this.friendPingTimes = puArray.remove(0).getInteger();
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}

	public int getPingTimes() {
		return pingTimes;
	}

	public void setPingTimes(int pingTimes) {
		this.pingTimes = pingTimes;
	}

	public int getFriendPingTimes() {
		return friendPingTimes;
	}

	public void setFriendPingTimes(int frindPingTimes) {
		this.friendPingTimes = frindPingTimes;
	}
}
