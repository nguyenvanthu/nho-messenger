package com.nho.server.test.friend;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.friend.statics.FriendField;
import com.nho.server.NhoServer;
import com.nho.server.data.UserMongoModel;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.statics.HandlerCollection;

public class FriendCounter {
	private NhoServer context ;
	private UserMongoModel userMongoModel;
	
	public NhoServer getContext() {
		return context;
	}

	public void setContext(NhoServer context) {
		this.context = context;
	}

	public FriendCounter() {
		this.context = new NhoServer();
	}
	
	public void getAverageFriend(){
		int average = 0 ;
		int max = 0 ;
		int min = 0 ;
		int count = 0;
		int sum = 0;
		List<UserMongoBean> userBeans = this.getUserMongoModel().findAllUser();
		for(UserMongoBean userBean : userBeans){
			//make sure they are new users 
			if(!userBean.getUserName().matches(".*[a-z].*")){
				int friends = getFriendsOfUser(userBean.getUserName());
				sum += friends;
				if(friends>max){
					max = friends;
				}
				if(friends<min){
					min = friends;
				}
				count +=1;
			}
		}
		average = sum/count;
		System.out.println("User has max friends is "+max);
		System.out.println("User has min friends is "+min);
		System.out.println("Average is "+average);
	}
	
	private int getFriendsOfUser(String userName){

		PuObject data = new PuObject();
//		data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_FRIEND.getCode());
		data.setString(FriendField.SENDER_NAME, userName);
		PuElement puElement = this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
		PuObject result = (PuObject) puElement;
		List<String> friends = new ArrayList<>();
		int status = result.getInteger(FriendField.STATUS);
		if (status == 0) {
			PuArray array = result.getPuArray(FriendField.LIST_FRIEND);
			if (array != null) {
				for (PuValue value : array) {
					friends.add(value.getString());
				}
			}
		}
		return friends.size();
	}
	

	protected UserMongoModel getUserMongoModel() {
		if (this.userMongoModel == null) {
			this.userMongoModel = getContext().getModelFactory().newModel(UserMongoModel.class);
		}
		return this.userMongoModel;
	}
}
