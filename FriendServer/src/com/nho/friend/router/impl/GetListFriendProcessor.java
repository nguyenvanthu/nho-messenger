package com.nho.friend.router.impl;

import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.friend.annotation.FriendCommandProcessor;
import com.nho.friend.exception.FriendException;
import com.nho.friend.router.FriendAbstractProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;

/**
 * get list friend of user A friend status = 0 && (friend.user = user A ||
 * friend.buddy = user A)
 */
@FriendCommandProcessor(command = { FriendCommand.GET_LIST_FRIEND })
public class GetListFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		List<String> friends = this.getFriendMongoModel().findFriendOfUser(senderUserName);
		PuArray array = new PuArrayList();
		for (String friend : friends) {
			array.addFrom(friend);
		}
		getLogger().debug("get {} friend from db", friends.size());
		response.setInteger(FriendField.STATUS, 0);
		response.setPuArray(FriendField.LIST_FRIEND, array);

		return response;
	}

}
