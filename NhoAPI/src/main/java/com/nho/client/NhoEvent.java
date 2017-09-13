package com.nho.client;

import com.nhb.eventdriven.impl.BaseEvent;
import com.nho.message.NhoMessage;

public class NhoEvent extends BaseEvent {

	private static final long serialVersionUID = 3943524544195661171L;

	private NhoMessage message;

	public NhoEvent(NhoMessage message) {
		this.setType(message.getType().name());
		this.message = message;
	}

	@SuppressWarnings("unchecked")
	public <T extends NhoMessage> T getMessage() {
		return (T) this.message;
	}
}
