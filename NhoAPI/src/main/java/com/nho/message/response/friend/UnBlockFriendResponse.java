package com.nho.message.response.friend;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class UnBlockFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.UNBLOCK_FRIEND_RESPONSE);
	}

	private boolean successful;
	private Error error;
	
	private String blockedUserName;
	private String blockedDisplayName;
	private String avatarBlockedName ;
	
	private String senderUserName ;
	private String senderDisplayName ;
	private String avatarSenderName ;

	public String getSenderUserName() {
		return senderUserName;
	}

	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}

	public String getSenderDisplayName() {
		return senderDisplayName;
	}

	public void setSenderDisplayName(String senderDisplayName) {
		this.senderDisplayName = senderDisplayName;
	}

	public String getAvatarSenderName() {
		return avatarSenderName;
	}

	public void setAvatarSenderName(String avatarSenderName) {
		this.avatarSenderName = avatarSenderName;
	}

	public String getAvatarBlockedName() {
		return avatarBlockedName;
	}

	public void setAvatarBlockedName(String avatarBlockedName) {
		this.avatarBlockedName = avatarBlockedName;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getBlockedUserName() {
		return blockedUserName;
	}

	public void setBlockedUserName(String blockedUserName) {
		this.blockedUserName = blockedUserName;
	}

	public String getBlockedDisplayName() {
		return blockedDisplayName;
	}

	public void setBlockedDisplayName(String blockedDisplayName) {
		this.blockedDisplayName = blockedDisplayName;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.blockedDisplayName);
			puArray.addFrom(this.blockedUserName);
			puArray.addFrom(this.avatarBlockedName);
			
			puArray.addFrom(this.senderDisplayName);
			puArray.addFrom(this.senderUserName);
			puArray.addFrom(this.avatarSenderName);
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			this.blockedDisplayName = puArray.remove(0).getString();
			this.blockedUserName = puArray.remove(0).getString();
			this.avatarBlockedName = puArray.remove(0).getString();
			
			this.senderDisplayName = puArray.remove(0).getString();
			this.senderUserName = puArray.remove(0).getString();
			this.avatarSenderName = puArray.remove(0).getString();
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
