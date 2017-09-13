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
import com.nho.friend.statics.FF;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.statics.StatusFriend;

@FriendCommandProcessor(command = { FriendCommand.MAKE_FRIEND })
public class MakeFriendProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		data.setType(FriendField.RECEIVER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		String receiverUserName = data.getString(FriendField.RECEIVER_NAME);
		String resultCheck = checkIsFriendExistedInDb(senderUserName, receiverUserName);
		if(!resultCheck.equals("")){
			if(resultCheck.equals(FF.ALREADY_SEND)){
				//update friend 
				this.getFriendMongoModel().updateFriendByUserName(senderUserName, receiverUserName, 0);
			}else {
				//already friend 
			}
			return PuObject.fromObject(new MapTuple<>(FriendField.STATUS,2));
		}
		FriendMongoBean friend = new FriendMongoBean();
		friend.setUser(senderUserName);
		friend.setBuddy(receiverUserName);
		friend.setStatus(0);
		boolean success = this.getFriendMongoModel().insertFriend(friend, false);
		if(success){
			return PuObject.fromObject(new MapTuple<>(FriendField.STATUS,0));
		}
		return PuObject.fromObject(new MapTuple<>(FriendField.STATUS,1));
	}
	private String checkIsFriendExistedInDb(String senderName, String receiverName) {
		String result = "";
		boolean isBlockFriend = false; // select in white list
		List<FriendMongoBean> friends = this.getFriendMongoModel().findFriendByUserName(senderName, receiverName,
				isBlockFriend);
		friends.addAll(this.getFriendMongoModel().findFriendByUserName(receiverName, senderName, isBlockFriend));
		if (friends.size() > 0) {
			FriendMongoBean bean = friends.get(0);
			if (bean != null) {
				if (bean.getStatus() == StatusFriend.ACCEPT.ordinal()) {
					result = FF.IS_FRIEND;
				} else {
					result = FF.ALREADY_SEND;
				}

			}
		}
		return result;
	}
}
