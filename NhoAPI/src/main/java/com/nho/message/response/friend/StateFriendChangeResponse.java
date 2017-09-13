package com.nho.message.response.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.StatusUser;

public class StateFriendChangeResponse extends NhoMessage{
	{
		this.setType(MessageType.STATE_FRIEND_CHANGE);
	}
	private String userName ;
	private StatusUser status ;
	private long lastTimeOnline ;
	
	public long getLastTimeOnline() {
		return lastTimeOnline;
	}
	public void setLastTimeOnline(long lastTimeOnline) {
		this.lastTimeOnline = lastTimeOnline;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public StatusUser getStatus() {
		return status;
	}
	public void setStatus(StatusUser status) {
		this.status = status;
	}
	
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.userName);
		puArray.addFrom(this.status.getCode());
		puArray.addFrom(this.lastTimeOnline);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.userName = puArray.remove(0).getString();
		this.status = StatusUser.fromCode(puArray.remove(0).getInteger());
		this.lastTimeOnline = puArray.remove(0).getLong();
	}
}
