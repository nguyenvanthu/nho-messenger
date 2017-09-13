package com.nho.message.request.login;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class UpdateProfile extends NhoMessage implements Request {
	{
		this.setType(MessageType.UPDATE_PROFILE);
	}

	private String newDisplayName;
	private String newAvatar;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.newDisplayName);
		puArray.addFrom(this.newAvatar);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.newDisplayName = puArray.remove(0).getString();
		this.newAvatar = puArray.remove(0).getString();
	}

	public String getNewDisplayName() {
		return newDisplayName;
	}

	public void setNewDisplayName(String newDisplayName) {
		this.newDisplayName = newDisplayName;
	}

	public String getNewAvatar() {
		return newAvatar;
	}

	public void setNewAvatar(String newAvatar) {
		this.newAvatar = newAvatar;
	}

}
