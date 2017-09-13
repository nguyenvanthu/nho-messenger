package com.nho.message.response.friend;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusFriend;

public class SendFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.SEND_FRIEND_RESPONSE);
	}

	private boolean successful;
	private StatusFriend statusFriend;
	private Error error;
	
	private String senderUserName;
	private String senderDisplayName;
	private String avatarSenderName ;

	private String receiverUserName ;
	private String receiverDisplayName ;
	private String avatarReceiverName ;
	
	public String getReceiverUserName() {
		return receiverUserName;
	}

	public void setReceiverUserName(String receiverUserName) {
		this.receiverUserName = receiverUserName;
	}

	public String getReceiverDisplayName() {
		return receiverDisplayName;
	}

	public void setReceiverDisplayName(String receiverDisplayName) {
		this.receiverDisplayName = receiverDisplayName;
	}

	public String getAvatarReceiverName() {
		return avatarReceiverName;
	}

	public void setAvatarReceiverName(String avatarReceiverName) {
		this.avatarReceiverName = avatarReceiverName;
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

	public StatusFriend getStatusFriend() {
		return statusFriend;
	}

	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if(this.successful){
			puArray.addFrom(this.statusFriend.ordinal());
			puArray.addFrom(this.senderUserName);
			puArray.addFrom(this.senderDisplayName);
			puArray.addFrom(this.avatarSenderName);
			
			puArray.addFrom(this.receiverDisplayName);
			puArray.addFrom(this.receiverUserName);
			puArray.addFrom(this.avatarReceiverName);
		}else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if(this.successful){
			this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
			this.senderUserName = puArray.remove(0).getString();
			this.senderDisplayName = puArray.remove(0).getString();
			this.avatarSenderName = puArray.remove(0).getString();
			
			this.receiverDisplayName = puArray.remove(0).getString();
			this.receiverUserName = puArray.remove(0).getString();
			this.avatarReceiverName = puArray.remove(0).getString();
		}else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}

}
