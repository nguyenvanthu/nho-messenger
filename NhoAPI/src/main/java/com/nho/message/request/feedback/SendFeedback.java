package com.nho.message.request.feedback;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class SendFeedback extends NhoMessage implements Request {
	{
		this.setType(MessageType.FEEDBACK_MESSAGE);
	}
	private String title;
	private String message;
	

	@Override
	protected void readPuArray(PuArray puArray) {
		this.title = puArray.remove(0).getString();
		this.message = puArray.remove(0).getString();
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.title);
		puArray.addFrom(this.message);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
