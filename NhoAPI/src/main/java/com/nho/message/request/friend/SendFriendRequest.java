package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;
/**
 * A send friend request to B 
 * senderUserName : userName of A 
 * receiverUserName : userName of B 
 * statusFriend : PENDING
 * @author trial
 *
 */
public class SendFriendRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.SEND_FRIEND_REQUEST);
	}

	private StatusFriend statusFriend;
	private String receiverUserName;
	private String senderUserName ;

	

	public String getSenderUserName() {
		return senderUserName;
	}

	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}

	public String getReceiverUserName() {
		return receiverUserName;
	}

	public void setReceiverUserName(String receiverUserName) {
		this.receiverUserName = receiverUserName;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.statusFriend.ordinal());
		puArray.addFrom(this.receiverUserName);
		puArray.addFrom(this.senderUserName);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
		this.receiverUserName = puArray.remove(0).getString();
		this.senderUserName = puArray.remove(0).getString();
	}

	public StatusFriend getStatusFriend() {
		return statusFriend;
	}

	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}

}
