package com.nho.server.processors.friend;

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
import com.nho.message.request.friend.GetListFriend;
import com.nho.message.response.friend.GetListFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.BotNho;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.Error;
import com.nho.statics.StatusUser;

/**
 * process request {@link GetListFriend} return response
 * {@link GetListFriendResponse} send command
 * {@link FriendCommand#GET_LIST_FRIEND} to
 * {@link com.nho.friend.router.impl.GetListFriendProcessor}
 */
@NhoCommandProcessor(command = { MessageType.GET_LIST_FRIEND })
public class GetListFriendProcessor extends AbstractNhoProcessor<GetListFriend> {

	@Override
	protected void process(GetListFriend request) {
		try {
			GetListFriendResponse response = new GetListFriendResponse();
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user not login ");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			getLogger().debug("start get list friend for user " + user.getUserName());
			long currentTime = System.currentTimeMillis();
			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_FRIEND.getCode());
			data.setString(FriendField.SENDER_NAME, request.getSenderUserName());
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
				List<String> displayNames = new ArrayList<>();
				List<String> avatarNames = new ArrayList<>();
				List<StatusUser> statusUsers = new ArrayList<>();
				List<Long> lastTimeOnlines = new ArrayList<>();
				for (String friend : friends) {
					getLogger().debug("friend {}", friend);
					if (friend.equals("bot")) {
						userNames.add(BotNho.USER_NAME);
						displayNames.add(BotNho.DISPLAY_NAME);
						avatarNames.add(BotNho.AVATAR);
						statusUsers.add(StatusUser.ONLINE);
						lastTimeOnlines.add((long) 0);
					} else {
						UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(friend);
						if (userBean == null) {
							getLogger().debug("userBean null ");
						} else {
							userNames.add(userBean.getUserName());
							displayNames.add(userBean.getDisplayName());
							avatarNames.add(userBean.getAvatar().getName());
							statusUsers.add(getStatusOfFriend(userBean.getUserName()));
							long lastTime = currentTime - userBean.getLastTimeOnline();
							lastTimeOnlines.add(lastTime);
							getLogger().debug("last time online of user {} is: {}", friend,
									userBean.getLastTimeOnline());
						}
					}
				}

				response.setSuccessful(true);
				response.setStatusFriend(request.getStatus());
				response.setUsernames(userNames);
				response.setDisplayNames(displayNames);
				response.setAvatarNames(avatarNames);
				response.setStatusUsers(statusUsers);
				response.setLastTimeOnlines(lastTimeOnlines);

				this.sendToUser(response, user);
			} else {
				getLogger().debug("something wrong in FriendServer with status = 1");
			}
		} catch (Exception exception) {
			getLogger().debug("error when get list friend " + exception);
		}

	}

	private StatusUser getStatusOfFriend(String userName) {
		StatusUser status = StatusUser.OFFLINE;
		User user = this.getUserManager().getUserByUserName(userName);
		if(user != null ){
			status = user.getStatus();
		}
		return status;
	}

	/** 
	 * sort friend list by last time chat 
	 * call to {@link SortFriendByLastTimeProcessor}
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
