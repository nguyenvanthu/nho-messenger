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
 * cancel friend between 2 user delete friend in friend database
 */
@FriendCommandProcessor(command = { FriendCommand.CANCEL_FRIEND })
public class CancelFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		data.setType(FriendField.ACCEPTER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		String cancelerUserName = data.getString(FriendField.CANCELER_NAME);

		long deleteCount = this.getFriendMongoModel().deleteFriend(cancelerUserName, senderUserName, false)
				+ this.getFriendMongoModel().deleteFriend(senderUserName, cancelerUserName, false);
		if (deleteCount != 0) {
			getLogger().debug("delete friend in database success");
			response.setInteger(FriendField.STATUS, 0);
			return response;
		} else {
			getLogger().debug("error when delete friend in db");
			response.setInteger(FriendField.STATUS, 1);
			response.setInteger(FriendField.ERROR, Error.CANCEL_FRIEND_ERROR.getCode());
			return response;
		}
	}

}
