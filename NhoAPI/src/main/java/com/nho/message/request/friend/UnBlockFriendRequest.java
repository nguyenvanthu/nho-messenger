package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
/**
 * B blocked A 
 * if B want to unblock A 
 * senderUserName : userName of user B 
 * blockedUserName : userName of user A 
 * @author trial
 *
 */
public class UnBlockFriendRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.UNBLOCK_FRIEND);
	}
	
	private String senderUserName ;
	private String blockedUserName ; // is blocked by sender
	
	public String getSenderUserName() {
		return senderUserName;
	}
	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}
	public String getBlockedUserName() {
		return blockedUserName;
	}
	public void setBlockedUserName(String blockedUserName) {
		this.blockedUserName = blockedUserName;
	}
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.senderUserName);
		puArray.addFrom(this.blockedUserName);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.senderUserName = puArray.remove(0).getString();
		this.blockedUserName = puArray.remove(0).getString();
	}
	
}
