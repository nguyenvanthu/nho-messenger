package com.nho.friend.router.impl;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.friend.annotation.FriendCommandProcessor;
import com.nho.friend.exception.FriendException;
import com.nho.friend.router.FriendAbstractProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.statics.Error;

/**
 * unblock friend delete friend in block_friend database
 */
@FriendCommandProcessor(command = { FriendCommand.UNBLOCK_FRIEND })
public class UnbockFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		data.setType(FriendField.BLOCKED_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		String blockedUserName = data.getString(FriendField.BLOCKED_NAME);
		boolean isBlockedFriend = true;
		long deletedCount = this.getFriendMongoModel().deleteFriend(senderUserName, blockedUserName, isBlockedFriend);

		if (deletedCount != 0) {
			getLogger().debug("delete successful in blocked friend collection");
			response.setInteger(FriendField.STATUS, 0);
			return response;
		} else {
			getLogger().debug("error when update status friend in db");
			response.setInteger(FriendField.STATUS, 1);
			response.setInteger(FriendField.ERROR, Error.UNBLOCK_FRIEND_ERROR.getCode());
			return response;
		}
	}

}
