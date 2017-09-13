package com.nho.server.reporter;

import java.util.Set;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.friend.statics.FriendCommand;
import com.nho.message.response.friend.StateFriendChangeResponse;
import com.nho.server.NhoServer;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.helper.FriendHelper;
import com.nho.server.processors.friend.GetListFriendProcessor;
import com.nho.statics.StatusUser;

public class BellHolder extends AbstractReporter {
	public BellHolder(NhoServer context) {
		super(context);
	}

	/**
	 * send command {@link FriendCommand#GET_LIST_FRIEND} to
	 * {@link GetListFriendProcessor} to get list friend send
	 * {@link StateFriendChangeResponse} with {@link StatusUser#ONLINE} to
	 * user's friend
	 */
	public void changeWhenUserOnline(String userName) {
		getLogger().debug("change when user {} online ", userName);
		FriendHelper helper = new FriendHelper(getContext());
		Set<String> friends = helper.getFriends(userName);
		getLogger().debug("number friend of user {} is {}", userName, friends.size());
		StateFriendChangeResponse response = new StateFriendChangeResponse();
		response.setStatus(StatusUser.ONLINE);
		response.setUserName(userName);
		response.setLastTimeOnline(0);
		// send stateFriendChangeResponse to all friend of user
		this.sendToUserNames(response, friends);
	}

	/**
	 * send command {@link FriendCommand#GET_LIST_FRIEND} to
	 * {@link GetListFriendProcessor} to get list friend send
	 * {@link StateFriendChangeResponse} with {@link StatusUser#OFFLINE} to
	 * user's friend
	 */
	public void changeWhenUserOffline(String userName) {
		FriendHelper helper = new FriendHelper(getContext());
		Set<String> friends = helper.getFriends(userName);
		StateFriendChangeResponse response = new StateFriendChangeResponse();
		response.setStatus(StatusUser.OFFLINE);
		response.setUserName(userName);
		UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(userName);
		if(userBean!=null){
			response.setLastTimeOnline(System.currentTimeMillis() - userBean.getLastTimeOnline());
		}else {
			response.setLastTimeOnline(System.currentTimeMillis() - 0);
		}
		// send stateFriendChangeResponse to all friend of user
		this.sendToUserNames(response, friends);
	}

	@Override
	public void cleanUserInfo(String sessionId) {
		User user = this.getUserManager().getUserBySessionId(sessionId);
		if (user != null) {
			String userName = user.getUserName();
			user.removeSession(sessionId);
			if (user.getSessions().size() <= 0) {
				getLogger().debug("user {} offline", userName);
				PuObject data = new PuObject();
				data.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_USER_CHANNEL.getCode());
				data.setString(ChatField.SENDER_NAME, userName);
				@SuppressWarnings("unused")
				RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
			}
		}
	}
}
