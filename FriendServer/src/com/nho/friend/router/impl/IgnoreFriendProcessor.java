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
 * ignore friend request delete friend in friend data base
 */
@FriendCommandProcessor(command = { FriendCommand.IGNORE_FRIEND })
public class IgnoreFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		data.setType(FriendField.IGNORER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		String ignorerUserName = data.getString(FriendField.IGNORER_NAME);

		long deleteCount = this.getFriendMongoModel().deleteFriend(senderUserName, ignorerUserName, false);
		getLogger().debug("deleteCount = " + deleteCount);
		if (deleteCount != 0) {
			getLogger().debug("update friend to ignored in white list success ");
			response.setInteger(FriendField.STATUS, 0);
			return response;
		} else {
			getLogger().debug("error when delete friend in db");
			response.setInteger(FriendField.STATUS, 1);
			response.setInteger(FriendField.ERROR, Error.IGNORE_FRIEND_ERROR.getCode());
			return response;
		}
	}
}
