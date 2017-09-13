package com.nho.friend.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.friend.annotation.FriendCommandProcessor;
import com.nho.friend.data.FriendMongoBean;
import com.nho.friend.exception.FriendException;
import com.nho.friend.router.FriendAbstractProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;

/**
 * get list invited friend (user send friend request to user A) of user A friend
 * status = 0 && friend.buddy = user A
 */

@FriendCommandProcessor(command = { FriendCommand.GET_LIST_INVITED })
public class GetListInvitedProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		int status = data.getInteger(FriendField.STATUS);

		boolean isBlockFriend = false;
		List<FriendMongoBean> friends = this.getFriendMongoModel().findFriendByBuddyUserNameAndStatus(senderUserName,
				status, isBlockFriend);
		List<String> buddys = new ArrayList<String>();
		for (FriendMongoBean friend : friends) {
			buddys.add(friend.getUser());
		}
		PuArray array = new PuArrayList();
		for (String buddy : buddys) {
			array.addFrom(buddy);
		}
		response.setInteger(FriendField.STATUS, 0);
		response.setPuArray(FriendField.LIST_FRIEND, array);
		return response;
	}

}
