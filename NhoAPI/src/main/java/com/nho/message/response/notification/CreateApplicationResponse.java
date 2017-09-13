package com.nho.message.response.notification;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class CreateApplicationResponse extends NhoMessage {
	{
		this.setType(MessageType.CREATE_APPLICATION_RESPONSE);
	}

	private PuObject data;
	private int status;
	private Error error;

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public PuObject getData() {
		return data;
	}

	public void setData(PuObject data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.data);
		puArray.addFrom(this.status);
		puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.data = puArray.remove(0).getPuObject();
		this.status = puArray.remove(0).getInteger();
		this.setError(Error.fromCode(puArray.remove(0).getInteger()));
	}
}
