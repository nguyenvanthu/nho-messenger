package com.nho.message;

public class MessageEvent {

	private Object data;

	public MessageEvent() {
		// do nothing
	}

	public MessageEvent(Object data) {
		this();
		this.setData(data);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}