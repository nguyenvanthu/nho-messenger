package com.nho.server.message;

import com.nhb.common.BaseLoggable;

public abstract class AbstractSendMessage extends BaseLoggable implements SendMessageable {

	@Override
	public void sendMessage(String email, String password, String title, String message) {
		try {
			this.process(email, password, title, message);
		} catch (Exception exception) {
			throw new RuntimeException("send message error ", exception);
		}
	}

	protected abstract void process(String email, String password, String title, String message);
}
