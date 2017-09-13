package com.nho.message.response.login;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class FakeLoginResponse extends NhoMessage{
	{
		this.setType(MessageType.FAKE_LOGIN_RESPONSE);
	}
	private boolean success;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.success);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
