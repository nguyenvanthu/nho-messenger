package com.nho.message.request.login;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class LoginFacebookRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.LOGIN_WITH_FACEBOOK);
	}
	
	private String facebookId ;
	private String facebookToken ;

	public String getFacebookToken() {
		return facebookToken;
	}

	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.facebookId);
		puArray.addFrom(this.facebookToken);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.facebookId = puArray.remove(0).getString();
		this.facebookToken = puArray.remove(0).getString();
	}
}
