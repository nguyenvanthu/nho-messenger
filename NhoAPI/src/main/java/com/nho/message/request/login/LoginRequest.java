package com.nho.message.request.login;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.AvatarType;

public class LoginRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.LOGIN);
	}
	private String facebookToken;
	private String displayName;
	private String avtUrl;
	private AvatarType avtType;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.facebookToken);
		puArray.addFrom(this.displayName);
		puArray.addFrom(this.avtUrl);
		puArray.addFrom(this.avtType.getCode());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.facebookToken = puArray.remove(0).getString();
		this.displayName = puArray.remove(0).getString();
		this.avtUrl = puArray.remove(0).getString();
		this.avtType = AvatarType.fromCode(puArray.remove(0).getInteger());
	}

	public String getFacebookToken() {
		return facebookToken;
	}

	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAvtUrl() {
		return avtUrl;
	}

	public void setAvtUrl(String avtUrl) {
		this.avtUrl = avtUrl;
	}

	public AvatarType getAvtType() {
		return avtType;
	}

	public void setAvtType(AvatarType avtType) {
		this.avtType = avtType;
	}
}
