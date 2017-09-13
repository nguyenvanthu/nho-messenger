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
 * get list pending friends of a given user (call user A) . A pending friend is
 * an user who receives a friend request by user A friend status == 0 & To
 * determine if an user 'friend' is a pending friend, we check for that user's
 * status equals 0 (specify the enum name for 0) and that user's "user" property
 * equals user A.
 */

@FriendCommandProcessor(command = { FriendCommand.GET_LIST_PENDDING })
public class GetListPenddingProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		int status = data.getInteger(FriendField.STATUS);

		boolean isBlockFriend = false;
		List<FriendMongoBean> friends = this.getFriendMongoModel().findFriendByUserNameAndStatus(senderUserName, status,
				isBlockFriend);
		List<String> buddys = new ArrayList<String>();
		for (FriendMongoBean friend : friends) {
			buddys.add(friend.getBuddy());
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
