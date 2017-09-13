package com.nho.message.response.login;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class LoginFacebookResponse extends NhoMessage {
	{
		this.setType(MessageType.LOGIN_WITH_FACEBOOK_RESPONSE);
	}
	private boolean success;
	private String token;
	private String displayName;
	private String avatarName;
	private Error error;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.success);
		if (this.success) {
			puArray.addFrom(this.token);
			puArray.addFrom(this.displayName);
			puArray.addFrom(this.avatarName);
		} else {
			puArray.addFrom(this.error == null ? null : this.error.getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
		if (this.success) {
			this.token = puArray.remove(0).getString();
			this.displayName = puArray.remove(0).getString();
			this.avatarName = puArray.remove(0).getString();
		} else {
			PuValue err = puArray.remove(0);
			if (err != null && err.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(err.getInteger()));
			}
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

}
