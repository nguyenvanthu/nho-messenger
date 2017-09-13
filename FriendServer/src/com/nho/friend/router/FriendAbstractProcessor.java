package com.nho.friend.router;

import com.nhb.common.BaseLoggable;
import com.nho.friend.FriendHandler;
import com.nho.friend.data.FriendMongoModel;

public abstract class FriendAbstractProcessor extends BaseLoggable implements FriendProcessor{
	private FriendHandler context ;
	private FriendMongoModel friendMongoModel;

	public FriendHandler getContext() {
		return context;
	}

	public void setContext(FriendHandler context) {
		this.context = context;
	}
	
	protected FriendMongoModel getFriendMongoModel() {
		if (this.friendMongoModel == null) {
			this.friendMongoModel = getContext().getModelFactory().newModel(FriendMongoModel.class);
		}
		return this.friendMongoModel;
	}
	
}
