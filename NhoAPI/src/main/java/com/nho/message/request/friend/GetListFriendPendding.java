package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;
/**
 * get list friend or friend request of user A 
 * senderUserName : userName of user A 
 * status
 * @author trial
 *
 */
public class GetListFriendPendding extends NhoMessage implements Request {
	{
		this.setType(MessageType.GET_LIST_FRIEND_PENDDING);
	}

	private String senderUserName;
	private StatusFriend status;

	public String getSenderUserName() {
		return senderUserName;
	}

	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}

	

	public StatusFriend getStatus() {
		return status;
	}

	public void setStatus(StatusFriend status) {
		this.status = status;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.senderUserName);
		puArray.addFrom(this.status.ordinal());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.senderUserName = puArray.remove(0).getString();
		this.status = StatusFriend.values()[puArray.remove(0).getInteger()];
	}
}
