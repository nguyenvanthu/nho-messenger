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
 * make friend between sender & accepter to friend data base
 */

@FriendCommandProcessor(command = { FriendCommand.ACCEPT_FRIEND})
public class AcceptFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		data.setType(FriendField.ACCEPTER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		String accepterUserName = data.getString(FriendField.ACCEPTER_NAME);
		int statusFriend = data.getInteger(FriendField.STATUS);

		long modifiedCount = this.getFriendMongoModel().updateFriendByUserName(senderUserName, accepterUserName,
				statusFriend);
		getLogger().debug("modified Count is " + modifiedCount);
		if (modifiedCount != 0) {
			getLogger().debug("update friend to accepted in white list success ");
			response.setInteger(FriendField.STATUS, 0);
			return response;
		} else {
			getLogger().debug("error when update status friend in db");
			response.setInteger(FriendField.STATUS, 1);
			response.setInteger(FriendField.ERROR, Error.ACCEPT_FRIEND_ERROR.getCode());
			return response;
		}
	}

}
