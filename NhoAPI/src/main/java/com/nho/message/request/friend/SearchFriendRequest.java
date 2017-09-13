package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;

public class SearchFriendRequest extends NhoMessage implements Request{
	{
		this.setType(MessageType.SEARCH_FRIEND);
	}
	private String userName ;
	private StatusFriend status ;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public StatusFriend getStatus() {
		return status;
	}
	public void setStatus(StatusFriend status) {
		this.status = status;
	}
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.userName);
		puArray.addFrom(this.status.ordinal());
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.userName = puArray.remove(0).getString();
		this.status = StatusFriend.values()[puArray.remove(0).getInteger()];
	}
}
