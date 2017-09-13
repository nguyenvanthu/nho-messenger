package com.nho.message.request.login;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class LogoutRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.LOGOUT);
	}
	private String deviceToken ;
	
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.deviceToken);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.deviceToken = puArray.remove(0).getString();
	}
}
