package com.nho.message.request;

import java.util.concurrent.atomic.AtomicLong;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class PingRequest extends NhoMessage implements Request {

	{
		super.setType(MessageType.PING);
	}

	private static final AtomicLong idSeed = new AtomicLong(0);

	private long id = idSeed.incrementAndGet();

	public long getId() {
		return this.id;
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
