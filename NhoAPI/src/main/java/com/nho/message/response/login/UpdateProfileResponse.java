package com.nho.message.response.login;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class UpdateProfileResponse extends NhoMessage {
	{
		this.setType(MessageType.UPDATE_PROFILE_RESPONSE);
	}
	private boolean success;
	private Error error;
	private String newDisplayName;
	private String newAvatarName;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.success);
		if (this.success) {
			puArray.addFrom(this.newAvatarName);
			puArray.addFrom(this.newDisplayName);
		} else {
			puArray.addFrom(this.error == null ? null : this.error.getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
		if (this.success) {
			this.newAvatarName = puArray.remove(0).getString();
			this.newDisplayName = puArray.remove(0).getString();
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

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public String getNewDisplayName() {
		return newDisplayName;
	}

	public void setNewDisplayName(String newDisplayName) {
		this.newDisplayName = newDisplayName;
	}

	public String getNewAvatarName() {
		return newAvatarName;
	}

	public void setNewAvatarName(String newAvatarName) {
		this.newAvatarName = newAvatarName;
	}

}
