package com.nho.message.response.connection;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class PongEvent extends NhoMessage {

	{
		super.setType(MessageType.PONG);
	}

	private long id;
	private long pingTime = -1;
	private long createdTime = System.nanoTime();

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPingTime(long pingTime) {
		if (this.pingTime > -1) {
			throw new RuntimeException("Unable to reset pingTime");
		}
		this.pingTime = pingTime;
	}

	public long getDelayNano() {
		if (this.pingTime < 0) {
			throw new RuntimeException("Ping time must be set before getDelay can be invoked");
		}
		return this.createdTime - this.pingTime;
	}

	public double getDelayMicro() {
		return new Double(this.getDelayNano()) / 1e3;
	}

	public double getDelayMillis() {
		return new Double(this.getDelayNano()) / 1e6;
	}

	public double getDelaySeconds() {
		return new Double(this.getDelayNano()) / 1e9;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.getId());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.id = puArray.remove(0).getLong();
	}
}
