package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;
/**
 * A send friend request to B 
 * if B want to ignore : B send IgnoreFriendRequest
 * ignorerUserName : userName of user B 
 * senderUserName : userName of user A 
 * statusFriend : IGNORE
 * @author trial
 *
 */
public class IgnoreFriendRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.IGNORE_FRIEND_REQUEST);
	}

	private String ignorerUserName ;
	private String senderUserName ;
	private StatusFriend statusFriend;
	
	public String getIgnorerUserName() {
		return ignorerUserName;
	}

	public void setIgnorerUserName(String ignorerUserName) {
		this.ignorerUserName = ignorerUserName;
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
		puArray.addFrom(this.ignorerUserName);
		puArray.addFrom(this.senderUserName);
		puArray.addFrom(this.statusFriend.ordinal());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.ignorerUserName = puArray.remove(0).getString();
		this.senderUserName = puArray.remove(0).getString();
		this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
	}
}
