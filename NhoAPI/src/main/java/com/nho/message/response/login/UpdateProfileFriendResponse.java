package com.nho.message.response.login;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class UpdateProfileFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.UPDATE_PROFILE_FRIEND_RESPONSE);
	}
	private String userName;
	private String newAvatar;
	private String newDisplayName;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.userName);
		puArray.addFrom(this.newAvatar);
		puArray.addFrom(this.newDisplayName);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.userName = puArray.remove(0).getString();
		this.newAvatar = puArray.remove(0).getString();
		this.newDisplayName = puArray.remove(0).getString();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNewAvatar() {
		return newAvatar;
	}

	public void setNewAvatar(String newAvatar) {
		this.newAvatar = newAvatar;
	}

	public String getNewDisplayName() {
		return newDisplayName;
	}

	public void setNewDisplayName(String newDisplayName) {
		this.newDisplayName = newDisplayName;
	}

}
