package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;
/**
 * A and B is friend 
 * if A want to unfriend B , A send CancelFriendRequest
 * cancelerUserName : userName of user A 
 * senderUserName : userName of user B 
 * statusFriend : CANCELED
 * @author trial
 *
 */
public class CancelFriendRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.CANCEL_FRIEND);
	}

	private String cancelerUserName ;
	private String senderUserName ;
	private StatusFriend statusFriend;
	
	public String getCancelerUserName() {
		return cancelerUserName;
	}

	public void setCancelerUserName(String cancelerUserName) {
		this.cancelerUserName = cancelerUserName;
	}

	public String getSenderUserName() {
		return senderUserName;
	}

	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}

	public StatusFriend getStatusFriend() {
		return statusFriend;
	}

	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.cancelerUserName);
		puArray.addFrom(this.senderUserName);
		puArray.addFrom(this.statusFriend.ordinal());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.cancelerUserName = puArray.remove(0).getString();
		this.senderUserName = puArray.remove(0).getString();
		this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
	}

}
