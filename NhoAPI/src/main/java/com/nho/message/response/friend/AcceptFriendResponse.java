package com.nho.message.response.friend;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusFriend;

public class AcceptFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.ACCEPT_FRIEND_RESPONSE);
	}

	private boolean successful;
	private Error error;
	private StatusFriend statusFriend;
	
	private String accepterUserName;
	private String accepterDisplayName;
	private String avatarAccepterName;
	
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

	public String getAvatarAccepterName() {
		return avatarAccepterName;
	}

	public void setAvatarAccepterName(String avatarAccepterName) {
		this.avatarAccepterName = avatarAccepterName;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public String getAccepterUserName() {
		return accepterUserName;
	}

	public void setAccepterUserName(String accepterUserName) {
		this.accepterUserName = accepterUserName;
	}

	public String getAccepterDisplayName() {
		return accepterDisplayName;
	}

	public void setAccepterDisplayName(String accepterDisplayName) {
		this.accepterDisplayName = accepterDisplayName;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public StatusFriend getStatusFriend() {
		return statusFriend;
	}

	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.accepterUserName);
			puArray.addFrom(this.accepterDisplayName);
			puArray.addFrom(this.statusFriend.ordinal());
			puArray.addFrom(this.avatarAccepterName);
			
			puArray.addFrom(this.senderUserName);
			puArray.addFrom(this.senderDisplayName);
			puArray.addFrom(this.avatarSenderName);
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			this.accepterUserName = puArray.remove(0).getString();
			this.accepterDisplayName = puArray.remove(0).getString();
			this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
			this.avatarAccepterName = puArray.remove(0).getString();
			
			this.senderUserName = puArray.remove(0).getString();;
			this.senderDisplayName = puArray.remove(0).getString();
			this.avatarSenderName = puArray.remove(0).getString();
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
