package com.nho.friend.router.impl;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.friend.annotation.FriendCommandProcessor;
import com.nho.friend.exception.FriendException;
import com.nho.friend.router.FriendAbstractProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;

@FriendCommandProcessor(command = { FriendCommand.DELETE_FRIEND })
public class DeleteFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		String userName = data.getString(FriendField.SENDER_NAME);
		long deleteCount = this.getFriendMongoModel().deleteFriendOfUser(userName);
		if (deleteCount > 0) {
			return PuObject.fromObject(new MapTuple<>(FriendField.STATUS, 0));
		}
		return PuObject.fromObject(new MapTuple<>(FriendField.STATUS, 1));
	}

}
