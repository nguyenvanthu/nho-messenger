package com.nho.statics;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class BlockFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.BLOCK_FRIEND_RESPONSE);
	}

	private boolean successful;
	private StatusFriend status;
	private Error error;
	
	private String senderUserName;
	private String senderDisplayName;
	private String avatarSenderName ;
	
	private String blockerUserName ;
	private String blockerDisplayName ;
	private String avatarBlockerName ;
	
	public String getBlockerUserName() {
		return blockerUserName;
	}

	public void setBlockerUserName(String blockerUserName) {
		this.blockerUserName = blockerUserName;
	}

	public String getBlockerDisplayName() {
		return blockerDisplayName;
	}

	public void setBlockerDisplayName(String blockerDisplayName) {
		this.blockerDisplayName = blockerDisplayName;
	}

	public String getAvatarBlockerName() {
		return avatarBlockerName;
	}

	public void setAvatarBlockerName(String avatarBlockerName) {
		this.avatarBlockerName = avatarBlockerName;
	}

	public String getAvatarSenderName() {
		return avatarSenderName;
	}

	public void setAvatarSenderName(String avatarSenderName) {
		this.avatarSenderName = avatarSenderName;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
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

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public StatusFriend getStatus() {
		return status;
	}

	public void setStatus(StatusFriend status) {
		this.status = status;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if(this.successful){
			puArray.addFrom(this.senderDisplayName);
			puArray.addFrom(this.senderUserName);
			puArray.addFrom(this.status.ordinal());
			puArray.addFrom(this.avatarSenderName);
			
			puArray.addFrom(this.blockerDisplayName);
			puArray.addFrom(this.blockerUserName);
			puArray.addFrom(this.avatarBlockerName);
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
			this.status = StatusFriend.values()[puArray.remove(0).getInteger()];
			this.avatarSenderName =  puArray.remove(0).getString();
			
			this.blockerDisplayName = puArray.remove(0).getString();
			this.blockerUserName = puArray.remove(0).getString();
			this.avatarBlockerName = puArray.remove(0).getString();
		}else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
