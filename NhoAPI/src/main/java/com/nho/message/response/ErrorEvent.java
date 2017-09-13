package com.nho.message.response;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class ErrorEvent extends NhoMessage {

	{
		this.setType(MessageType.ERROR);
	}

	private Error error;
	private String message;

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.error == null ? null : this.error.getCode());
		puArray.addFrom(this.message);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.error = Error.fromCode(puArray.remove(0).getInteger());
		this.message = puArray.remove(0).getString();
	}
}
