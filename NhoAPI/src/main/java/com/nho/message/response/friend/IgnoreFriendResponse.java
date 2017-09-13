package com.nho.message.response.friend;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusFriend;

public class IgnoreFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.IGNORE_FRIEND_RESPONSE);
	}

	private boolean successful;
	private StatusFriend statusFriend;
	private Error error ;
	
	private String ignoreUsername;
	private String ignoreDisplayName;
	private String avatarInogerName ;
	
	private String senderUserName ;
	private String senderDisplayName ;
	private String avatarSenderName;
	
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

	public String getAvatarInogerName() {
		return avatarInogerName;
	}

	public void setAvatarInogerName(String avatarInogerName) {
		this.avatarInogerName = avatarInogerName;
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

	public String getIgnoreUsername() {
		return ignoreUsername;
	}

	public void setIgnoreUsername(String ignoreUsername) {
		this.ignoreUsername = ignoreUsername;
	}

	public String getIgnoreDisplayName() {
		return ignoreDisplayName;
	}

	public void setIgnoreDisplayName(String ignoreDisplayName) {
		this.ignoreDisplayName = ignoreDisplayName;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if(this.successful){
			puArray.addFrom(this.ignoreUsername);
			puArray.addFrom(this.ignoreDisplayName);
			puArray.addFrom(this.statusFriend.ordinal());
			puArray.addFrom(this.avatarInogerName);
			
			puArray.addFrom(this.senderDisplayName);
			puArray.addFrom(this.senderUserName);
			puArray.addFrom(this.avatarSenderName);
		}else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if(this.successful){
			this.ignoreUsername = puArray.remove(0).getString();
			this.ignoreDisplayName = puArray.remove(0).getString();
			this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
			this.avatarInogerName = puArray.remove(0).getString();
			
			this.senderDisplayName = puArray.remove(0).getString();
			this.senderUserName = puArray.remove(0).getString();
			this.avatarSenderName = puArray.remove(0).getString();
		}else {
			PuValue error = puArray.remove(0);
			if(error!=null && error.getType() != PuDataType.NULL){
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
