package com.nho.friend.router.impl;

import java.util.List;

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
import com.nho.statics.Error;

/**
 * block friend between 2 user delete friend in friend database add friend to
 * block_friend data base
 */
@FriendCommandProcessor(command = { FriendCommand.BLOCK_FRIEND })
public class BlockFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.BLOCKER_NAME, PuDataType.STRING);
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		String blockerUserName = data.getString(FriendField.BLOCKER_NAME);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		int statusFriend = data.getInteger(FriendField.STATUS);

		if (!checkIsFriendExistedInBlockFriendCollection(senderUserName, blockerUserName)) {
			if (insertToListBlockFriend(senderUserName, blockerUserName, statusFriend)) {
				getLogger().debug("insert to database blacklist success");
				response.setInteger(FriendField.STATUS, 0);
				return response;
			} else {
				getLogger().debug("error when insert to black list");
				response.setInteger(FriendField.STATUS, 1);
				response.setInteger(FriendField.ERROR, Error.CANNOT_BLOCK_FRIEND_ERROR.getCode());
				return response;
			}
		} else {
			getLogger().debug("error , already blocked ");
			response.setInteger(FriendField.STATUS, 1);
			response.setInteger(FriendField.ERROR, Error.ALREADY_BLOCKED_ERROR.getCode());
			return response;
		}
	}

	private FriendMongoBean getBlockFriend(String senderUserName, String blockerUserName, int status) {
		FriendMongoBean friend = new FriendMongoBean();
		friend.setStatus(status);
		friend.setUser(blockerUserName);
		friend.setBuddy(senderUserName);

		return friend;
	}

	private void deleteInFriendCollection(String senderUserName, String blockerUserName) {

		boolean isBlockFriend = false;
		long deleteCount = this.getFriendMongoModel().deleteFriend(senderUserName, blockerUserName, isBlockFriend);
		getLogger().debug("deleteCount = " + deleteCount);
	}

	private boolean checkIsFriendExistedInBlockFriendCollection(String senderName, String receiverName) {
		deleteInFriendCollection(senderName, receiverName);
		boolean isExist = false;
		boolean isBlockFriend = true;
		List<FriendMongoBean> blocked = this.getFriendMongoModel().findFriendByNamesAndStatus(senderName, receiverName,
				4, isBlockFriend);
		blocked.addAll(
				this.getFriendMongoModel().findFriendByNamesAndStatus(receiverName, senderName, 4, isBlockFriend));
		if (blocked.size() > 0) {
			FriendMongoBean bean = blocked.get(0);
			if (bean != null) {
				isExist = true;
			}
		}
		return isExist;
	}

	private boolean insertToListBlockFriend(String senderUserName, String blockerUserName, int status) {
		boolean isSuccessful = false;
		FriendMongoBean friendBlock = getBlockFriend(senderUserName, blockerUserName, status);
		boolean isBlockFriend = true;
		if (this.getFriendMongoModel().insertFriend(friendBlock, isBlockFriend)) {
			isSuccessful = true;
		}
		return isSuccessful;
	}
}
