package com.nho.message.request.notification;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class RegisterPushNotificationRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.REGISTER_PUSH_NOTIFICATION);
	}

	private String token;


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.token);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.token = puArray.remove(0).getString();
	}

}
