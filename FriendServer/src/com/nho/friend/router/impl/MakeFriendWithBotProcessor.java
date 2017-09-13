package com.nho.friend.router.impl;

import java.util.List;

import com.nhb.common.data.MapTuple;
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
import com.nho.statics.StatusFriend;

@FriendCommandProcessor(command = { FriendCommand.MAKE_FRIEND_WITH_BOT })
public class MakeFriendWithBotProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		data.setType(FriendField.RECEIVER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		String receiverUserName = data.getString(FriendField.RECEIVER_NAME);
		FriendMongoBean friend = new FriendMongoBean();
		friend.setUser(senderUserName);
		friend.setBuddy(receiverUserName);
		friend.setStatus(0);
		if (!checkIsFriendExistedInDb(senderUserName, receiverUserName)) {
			if (this.getFriendMongoModel().insertFriend(friend, false)) {
				getLogger().debug("insert friend with bot to db success");
				return PuObject.fromObject(new MapTuple<>(FriendField.STATUS, 0));
			}
		}
		getLogger().debug("already is friend with bot ");
		return PuObject.fromObject(new MapTuple<>(FriendField.STATUS, 1));
	}

	private boolean checkIsFriendExistedInDb(String senderName, String receiverName) {
		boolean isExist = false;
		boolean isBlockFriend = false; // select in white list
		List<FriendMongoBean> friends = this.getFriendMongoModel().findFriendByUserName(senderName, receiverName,
				isBlockFriend);
		friends.addAll(this.getFriendMongoModel().findFriendByUserName(receiverName, senderName, isBlockFriend));
		if (friends.size() > 0) {
			FriendMongoBean bean = friends.get(0);
			if (bean != null) {
				if (bean.getStatus() == StatusFriend.ACCEPT.ordinal()) {
					isExist = true;
					getLogger().debug("friend is exist in db");
				}
			}
		}
		
		return isExist;
	}
}
