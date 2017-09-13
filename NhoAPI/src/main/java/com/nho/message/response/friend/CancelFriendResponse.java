package com.nho.message.response.friend;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusFriend;

public class CancelFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.CANCEL_FRIEND_RESPONSE);
	}

	private boolean successful;
	private Error error;
	private StatusFriend statusFriend;
	
	private String senderUserName;
	private String senderDisplayName;
	private String avatarSenderName ;
	
	private String cancelerUserName ;
	private String cancelerDisplayName ;
	private String avatarCancelerName ;

	public String getAvatarSenderName() {
		return avatarSenderName;
	}

	public void setAvatarSenderName(String avatarSenderName) {
		this.avatarSenderName = avatarSenderName;
	}

	public String getCancelerUserName() {
		return cancelerUserName;
	}

	public void setCancelerUserName(String cancelerUserName) {
		this.cancelerUserName = cancelerUserName;
	}

	public String getCancelerDisplayName() {
		return cancelerDisplayName;
	}

	public void setCancelerDisplayName(String cancelerDisplayName) {
		this.cancelerDisplayName = cancelerDisplayName;
	}

	public String getAvatarCancelerName() {
		return avatarCancelerName;
	}

	public void setAvatarCancelerName(String avatarCancelerName) {
		this.avatarCancelerName = avatarCancelerName;
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

	public StatusFriend getStatusFriend() {
		return statusFriend;
	}

	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}

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

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if(this.successful){
			puArray.addFrom(this.senderDisplayName);
			puArray.addFrom(this.senderUserName);
			puArray.addFrom(this.statusFriend.ordinal());
			puArray.addFrom(this.avatarSenderName);
			
			puArray.addFrom(this.cancelerDisplayName);
			puArray.addFrom(this.cancelerUserName);
			puArray.addFrom(this.avatarCancelerName);
		}else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if(this.successful){
			this.senderDisplayName = puArray.remove(0).getString();
			this.senderUserName = puArray.remove(0).getString();
			this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
			this.avatarSenderName = puArray.remove(0).getString();
			
			this.cancelerDisplayName = puArray.remove(0).getString();
			this.cancelerUserName = puArray.remove(0).getString();
			this.avatarCancelerName = puArray.remove(0).getString();
		}else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
