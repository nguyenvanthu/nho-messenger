package com.nho.friend.router.impl;

import java.util.List;

import org.bson.Document;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.friend.annotation.FriendCommandProcessor;
import com.nho.friend.data.FriendMongoBean;
import com.nho.friend.exception.FriendException;
import com.nho.friend.router.FriendAbstractProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendDbFields;
import com.nho.statics.F;

@FriendCommandProcessor(command = { FriendCommand.UPDATE_FRIEND_DB })
public class UpdateFriendDbProcessor extends FriendAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FriendException {
		List<Document> friendDocuments = this.getFriendMongoModel().getAllFriendDocument(false);
		for(Document document : friendDocuments){
			this.getFriendMongoModel().deleteFriendDocument(document, false);
			FriendMongoBean bean = new FriendMongoBean();
			bean.setObjectId(document.getObjectId(FriendDbFields._ID));
			bean.setCreatedTime(document.getString(F.CREATED_TIME));
			bean.setStatus(document.getInteger(F.STATUS));
			bean.setUser(document.getString("user.userName"));
			bean.setBuddy(document.getString("buddy.userName"));
			this.getFriendMongoModel().insertFriend(bean, false);
		}
		return null;
	}

}
