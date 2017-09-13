//package com.nho.server.helper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.nhb.common.data.MapTuple;
//import com.nhb.common.data.PuArray;
//import com.nhb.common.data.PuElement;
//import com.nhb.common.data.PuObject;
//import com.nhb.common.data.PuValue;
//import com.nho.friend.statics.FriendCommand;
//import com.nho.friend.statics.FriendField;
//import com.nho.server.AdminHandler;
//import com.nho.server.data.UserMongoModel;
//import com.nho.server.data.user.UserMongoBean;
//import com.nho.server.statics.HandlerCollection;
//import com.nho.statics.F;
//
//public class FriendCounter {
//	private AdminHandler context;
//	private UserMongoModel userMongoModel;
//
//	public AdminHandler getContext() {
//		return context;
//	}
//
//	public void setContext(AdminHandler context) {
//		this.context = context;
//	}
//
//	public FriendCounter(AdminHandler context) {
//		this.context = context;
//	}
//
//	public PuObject getAverageFriend() {
//		PuObject result = new PuObject();
//		int average = 0;
//		int max = 0;
//		int min = 1;
//		int count = 0;
//		int sum = 0;
//		List<UserMongoBean> userBeans = this.getUserMongoModel().findAllUser();
//		for (UserMongoBean userBean : userBeans) {
//			// make sure they are new users
//			if (!userBean.getUserName().matches(".*[a-z].*")) {
//				int friends = getFriendsOfUser(userBean.getUserName());
//				sum += friends;
//				if (friends > max) {
//					max = friends;
//				}
//				if (friends < min) {
//					min = friends;
//				}
//				count += 1;
//			}
//		}
//		if (count == 0) {
//			return PuObject.fromObject(new MapTuple<>(F.STATUS, "Khong tim thay friend nao"));
//		}
//		average = sum / count;
//		result.setInteger("users", count);
//		result.setInteger("max", max);
//		result.setInteger("min", min);
//		result.setInteger("average", average);
//
//		return result;
//	}
//
//	private int getFriendsOfUser(String userName) {
//
//		PuObject data = new PuObject();
//		data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_FRIEND.getCode());
//		data.setString(FriendField.SENDER_NAME, userName);
//		PuElement puElement = this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
//		PuObject result = (PuObject) puElement;
//		List<String> friends = new ArrayList<>();
//		int status = result.getInteger(FriendField.STATUS);
//		if (status == 0) {
//			PuArray array = result.getPuArray(FriendField.LIST_FRIEND);
//			if (array != null) {
//				for (PuValue value : array) {
//					friends.add(value.getString());
//				}
//			}
//		}
//		return friends.size();
//	}
//
//	protected UserMongoModel getUserMongoModel() {
//		if (this.userMongoModel == null) {
//			this.userMongoModel = getContext().getModelFactory().newModel(UserMongoModel.class);
//		}
//		return this.userMongoModel;
//	}
//}
