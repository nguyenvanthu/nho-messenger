package com.nho.message.request.friend;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.StatusFriend;
/**
 * get list user send friend request to user A 
 * userName : userName of user A 
 * @author trial
 *
 */
public class GetListInviteFriend extends NhoMessage implements Request {
	{
		this.setType(MessageType.GET_LIST_INVITE_FRIEND);
	}

	private StatusFriend statusFriend;
	private String userName ;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public StatusFriend getStatusFriend() {
		return statusFriend;
	}

	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}


	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.userName);
		puArray.addFrom(this.statusFriend.ordinal());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		getLogger().debug("size of puArray "+puArray.size());
		this.userName = puArray.remove(0).getString();
		this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
	}
}
