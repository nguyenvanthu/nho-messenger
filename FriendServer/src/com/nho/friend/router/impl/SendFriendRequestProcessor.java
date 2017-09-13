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
import com.nho.statics.Error;
import com.nho.statics.StatusFriend;

/**
 * send friend request from user A to user B add friend of user A and B with
 * friend status == 0
 */

@FriendCommandProcessor(command = { FriendCommand.SEND_FRIEND_REQUEST })
public class SendFriendRequestProcessor extends FriendAbstractProcessor {
	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(FriendField.SENDER_NAME, PuDataType.STRING);
		data.setType(FriendField.RECEIVER_NAME, PuDataType.STRING);
		String senderUserName = data.getString(FriendField.SENDER_NAME);
		String receiverUserName = data.getString(FriendField.RECEIVER_NAME);
		int status = request.getInteger(FriendField.STATUS);
		getLogger().debug("user {} send friend request to user {}", senderUserName, receiverUserName);
		if (receiverUserName == null || senderUserName == null) {
			getLogger().debug("error : receiver is null => can not send friend request");
			response.set(FriendField.STATUS, 1);
			response.setInteger(FriendField.ERROR, Error.SEND_FRIEND_REQUEST_NOT_ENOUGH_INFO.getCode());
			return response;
		}
		if (receiverUserName.equals(senderUserName)) {
			getLogger().debug("error receiver and sender is duplicated");
			response.set(FriendField.STATUS, 1);
			response.setInteger(FriendField.ERROR, Error.SEND_FRIEND_REQUEST_ITSELF.getCode());
			return response;
		}
		String resultCheck = checkIsFriendExistedInDb(senderUserName, receiverUserName);
		if (!resultCheck.equals("")) {
			getLogger().debug("error already sended friend request or is friend or blocked");
			response.set(FriendField.STATUS, 1);
			if (resultCheck.equals(FF.BLOCKED)) {
				response.setInteger(FriendField.ERROR, Error.USER_BLOCKED.getCode());
			} else if (resultCheck.equals(FF.ALREADY_SEND)) {
				response.setInteger(FriendField.ERROR, Error.ALREADY_SEND_FRIEND_REQUEST.getCode());
			} else {
				response.setInteger(FriendField.ERROR, Error.ALREADY_FRIEND.getCode());
			}
			return response;
		}
		FriendMongoBean insertedFriend = getFriendBeanInsert(senderUserName, receiverUserName, status);
		boolean isBlockFriend = false;

		if (this.getFriendMongoModel().insertFriend(insertedFriend, isBlockFriend)) {
			getLogger().debug("add friend to white list success ");
			response.setInteger(FriendField.STATUS, 0);
			return response;
		}
		return PuObject.fromObject(new MapTuple<>(FriendField.STATUS, 1));
	}

	/**
	 * check in db : if A sended B or B sended A or A and B is friend -> return
	 * true else return false
	 * 
	 * @param senderId
	 * @param receiverId
	 * @return
	 */
	private String checkIsFriendExistedInDb(String senderName, String receiverName) {
		String result = "";
		// if (isBlocked(senderName, receiverName)) {
		// result = FF.BLOCKED;
		// return result;
		// }
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

	/**
	 * find in black list if 2 user block another -> cannot send friend request
	 */
	// private boolean isBlocked(String senderName, String receiverName) {
	// boolean isBlock = false;
	// boolean isBlockFriend = true;
	// List<FriendMongoBean> result =
	// this.getFriendMongoModel().findFriendByNamesAndStatus(receiverName,
	// senderName,
	// 4, isBlockFriend);
	// result.addAll(
	// this.getFriendMongoModel().findFriendByNamesAndStatus(senderName,
	// receiverName, 4, isBlockFriend));
	// if (result.size() > 0) {
	// for(FriendMongoBean friend : result){
	// if((friend.getUser().equals(senderName)&&
	// friend.getBuddy().equals(receiverName))
	// ||(friend.getUser().equals(receiverName)&&
	// friend.getBuddy().equals(senderName))){
	// isBlock = true ;
	// }
	// }
	// }
	// getLogger().debug("user {} & {} are block another
	// ",senderName,receiverName);
	// return isBlock;
	// }

	private FriendMongoBean getFriendBeanInsert(String senderUserName, String receiverUserName, int statusFriend) {
		FriendMongoBean friend = new FriendMongoBean();
		friend.setBuddy(receiverUserName);
		friend.setUser(senderUserName);
		friend.setStatus(statusFriend);

		return friend;
	}
}
