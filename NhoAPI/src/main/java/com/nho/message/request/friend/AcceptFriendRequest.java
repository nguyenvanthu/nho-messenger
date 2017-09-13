package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;

/**
 * if A send friend request to B and B accept friend request of A B send
 * AcceptFriendRequest accepterUserName : userName of user B senderUserName :
 * userName of B statusFriend : ACCEPTED
 * 
 * @author trial
 *
 */
public class AcceptFriendRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.ACCEPT_FRIEND_REQUEST);
	}

	private String accepterUserName;
	private String senderUserName;
	private StatusFriend statusFriend;

	public String getAccepterUserName() {
		return accepterUserName;
	}

	public void setAccepterUserName(String accepterUserName) {
		this.accepterUserName = accepterUserName;
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
		puArray.addFrom(this.accepterUserName);
		puArray.addFrom(this.senderUserName);
		puArray.addFrom(this.statusFriend.ordinal());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.accepterUserName = puArray.remove(0).getString();
		this.senderUserName = puArray.remove(0).getString();
		this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
	}
}
