package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;
/**
 * if user B want to block user A 
 * B send BlockFriendRequest 
 * blockUserName : userName of user B
 * senderUserName : userName of A 
 * statusFriend : StatusFriend.BLOCKED
 * @author trial
 *
 */
public class BlockFriendRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.BLOCK_FRIEND);
	}

	private String blockerUserName ;
	private String senderUserName ;
	private StatusFriend statusFriend;
	
	public String getBlockerUserName() {
		return blockerUserName;
	}

	public void setBlockerUserName(String blockerUserName) {
		this.blockerUserName = blockerUserName;
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
		puArray.addFrom(this.blockerUserName);
		puArray.addFrom(this.senderUserName);
		puArray.addFrom(this.statusFriend.ordinal());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.blockerUserName = puArray.remove(0).getString();
		this.senderUserName = puArray.remove(0).getString();
		this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
	}

}
