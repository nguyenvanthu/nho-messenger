package com.nho.server.processors.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.chat.router.impl.SortFriendByLastTimeProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.login.ReturnAppRequest;
import com.nho.message.response.friend.StateFriendChangeResponse;
import com.nho.message.response.login.ReturnAppResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.BotNho;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;
import com.nho.statics.StatusUser;

@NhoCommandProcessor(command = { MessageType.RETURN_APP })
public class ReturnAppProcessor extends AbstractNhoProcessor<ReturnAppRequest> {

	@Override
	protected void process(ReturnAppRequest request) throws Exception {
		getLogger().debug("receive return app request");
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		ReturnAppResponse response = new ReturnAppResponse();
		if (user == null) {
			getLogger().debug("error user not login");
			response.setSuccess(false);
			response.setError(Error.USER_NOT_LOGGED_IN);
			this.send(response, request.getSessionId());
			return;
		}
		long currentTime = System.currentTimeMillis();
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_FRIEND.getCode());
		data.setString(FriendField.SENDER_NAME, user.getUserName());
		PuElement puElement = this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
		PuObject result = (PuObject) puElement;
		int status = result.getInteger(FriendField.STATUS);
		if (status == 0) {
			getLogger().debug("receive friends response from Friend Server");
			PuArray array = result.getPuArray(FriendField.LIST_FRIEND);
			List<String> friends = new ArrayList<>();
			if (array != null) {
				for (PuValue value : array) {
					friends.add(value.getString());
				}
			}
			friends = sortByLastTimeChat(friends, user.getUserName());
			getLogger().debug("number friend in list friend " + friends.size());
			List<String> userNames = new ArrayList<>();
			List<StatusUser> statusUsers = new ArrayList<>();
			List<Long> lastTimeOnlines = new ArrayList<>();
			for (String friend : friends) {
				getLogger().debug("friend {}", friend);
				if (friend.equals("bot")) {
					userNames.add(BotNho.USER_NAME);
					statusUsers.add(StatusUser.ONLINE);
					lastTimeOnlines.add((long) 0);
				} else {
					UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(friend);
					if (userBean == null) {
						getLogger().debug("userBean null ");
					} else {
						userNames.add(userBean.getUserName());
						statusUsers.add(getStatusOfFriend(userBean.getUserName()));
						long lastTime = currentTime - userBean.getLastTimeOnline();
						lastTimeOnlines.add(lastTime);
						getLogger().debug("last time online of user {} is: {}", friend, userBean.getLastTimeOnline());
					}
				}
			}

			response.setSuccess(true);
			response.setFriends(userNames);
			response.setStatusFriends(statusUsers);
			response.setLastTimeOnlines(lastTimeOnlines);

			this.sendToUser(response, user);
			
			StateFriendChangeResponse friendReturn = new StateFriendChangeResponse();
			friendReturn.setStatus(StatusUser.ONLINE);
			friendReturn.setUserName(user.getUserName());
			friendReturn.setLastTimeOnline(0);
			this.sendToUserNames(friendReturn, userNames);
			
		} else {
			getLogger().debug("something wrong in FriendServer with status = 1");
		}
	}

	private StatusUser getStatusOfFriend(String userName) {
		StatusUser status = StatusUser.OFFLINE;
		User user = this.getUserManager().getUserByUserName(userName);
		if (user != null) {
			status = user.getStatus();
		}
		return status;
	}

	/**
	 * sort friend list by last time chat call to
	 * {@link SortFriendByLastTimeProcessor}
	 */
	private List<String> sortByLastTimeChat(List<String> friends, String userName) {
		Collections.sort(friends);
		PuArray friendArray = new PuArrayList();
		for (String friend : friends) {
			friendArray.addFrom(friend);
		}
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.SORT_FRIEND_BY_TIME.getCode());
		data.setPuArray(ChatField.FRIENDS, friendArray);
		data.setString(ChatField.USER_NAME, userName);

		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		PuArray sortedFriendArray = result.getPuArray(ChatField.FRIENDS);
		List<String> sortedFriend = new ArrayList<>();
		for (PuValue value : sortedFriendArray) {
			sortedFriend.add(value.getString());
		}
		return sortedFriend;
	}
}
