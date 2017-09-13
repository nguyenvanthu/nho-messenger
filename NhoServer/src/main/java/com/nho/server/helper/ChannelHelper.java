package com.nho.server.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.chat.router.impl.CheckReceiverBusyProcessor;
import com.nho.chat.router.impl.CheckUserBusyProcessor;
import com.nho.chat.router.impl.GetChannelProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.friend.statics.FriendField;
import com.nho.server.NhoServer;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.StatusUser;

/**
 * class contains commons task about channel
 */
public class ChannelHelper extends AbstractHelper {
	public ChannelHelper(NhoServer context) {
		super.setContext(context);
	}

	public boolean isUserOffline(String userName) {
		StatusUser status = this.getContext().getUserManager().getStatusUser(userName);
		if(status == StatusUser.OFFLINE){
			return true ;
		}
		return false;
	}

	public boolean isUserLogout(String userName) {
		StatusUser status = this.getContext().getUserManager().getStatusUser(userName);
		if(status == StatusUser.OFFLINE){
			return true ;
		}
		return false;
	}

	public boolean isUserChatWithBot(String userName) {
		if (this.getContext().getUserManager().isChatWithBot(userName)) {
			return true;
		}
		return false;
	}

	public boolean isRecieverOnline(String channelId, String sender) {
		for (String receiver : getUsersInChannelByChannelId(channelId, sender)) {
			if (isUserOnline(receiver)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get rest user in channel send command {@link ChannelCommand#GET_CHANNEL}
	 * to {@link GetChannelProcessor}
	 */
	public Set<String> getUsersInChannelByChannelId(String channelId, String userName) {
		Set<String> subcribers = new HashSet<>();
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, ChannelCommand.GET_CHANNEL.getCode());
		data.setString(ChatField.CHANNEL_ID, channelId);
		// using rabbitMQ
		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			int status = result.getInteger(ChatField.STATUS);
			if (status == 0) {
				PuArray array = result.getPuArray(ChatField.SUBCRIBE);
				for (PuValue value : array) {
					if (!value.getString().equals(userName)) {
						subcribers.add(value.getString());
					}
				}
			}

		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
		}
		return subcribers;
	}

	public Set<String> getUserInChannel(String channelId) {
		Set<String> subcribers = new HashSet<>();
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, ChannelCommand.GET_CHANNEL.getCode());
		data.setString(ChatField.CHANNEL_ID, channelId);
		// using rabbitMQ
		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			int status = result.getInteger(ChatField.STATUS);
			if (status == 0) {
				PuArray array = result.getPuArray(ChatField.SUBCRIBE);
				for (PuValue value : array) {
					subcribers.add(value.getString());
				}
			}

		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
		}
		return subcribers;
	}

	private boolean isUserOnline(String userName) {
		StatusUser status = this.getContext().getUserManager().getStatusUser(userName);
		if(status == StatusUser.ONLINE){
			return true ;
		}
		return false;
	}

	public boolean isChannelChatWithBot(String channelId) {
		boolean isWithBot = false;
		if (channelId.contains("bot")) {
			isWithBot = true;
		}

		return isWithBot;
	}

	/**
	 * return online user in channel
	 */
	public Set<String> getOnlineUsersInChannel(String channelId, String userName) {
		Set<String> subcribers = getUsersInChannelByChannelId(channelId, userName);
		getLogger().debug("number user in channel " + subcribers.size());
		Set<String> userOnlines = new HashSet<>();
		for (String subcriber : subcribers) {
			if (isUserOnline(subcriber) && !subcriber.equals(userName)) {
				userOnlines.add(subcriber);
			}
		}
		return userOnlines;
	}

	/**
	 * check user invitedUserName is busy when chat with user userName send
	 * command {@link ChannelCommand#CHECK_USER_BUSY} to
	 * {@link CheckUserBusyProcessor}
	 */
	public boolean isUserBusy(String channelId, String invitedUserName) {
		boolean isReceiverBuzy = false;
		PuObject dataCheckUserBuzy = new PuObject();
		dataCheckUserBuzy.setInteger(ChatField.COMMAND, ChannelCommand.CHECK_USER_BUSY.getCode());
		dataCheckUserBuzy.setString(ChatField.CHANNEL_ID, channelId);
		dataCheckUserBuzy.setString(ChatField.INVITED_USER, invitedUserName);
		PuObject resultCheckUserBusy = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER,
				dataCheckUserBuzy);
		isReceiverBuzy = resultCheckUserBusy.getBoolean(ChatField.IS_BUSY);
		return isReceiverBuzy;
	}

	/**
	 * check user A in channel chat with user B is busy ? send command
	 * {@link ChannelCommand#CHECK_RECEIVER_BUSY} to
	 * {@link CheckReceiverBusyProcessor}
	 */
	public boolean isReceiverIsBusy(String channelId, String from) {
		boolean isReceiverBuzy = false;
		PuObject dataCheckUserBuzy = new PuObject();
		dataCheckUserBuzy.setInteger(ChatField.COMMAND, ChannelCommand.CHECK_RECEIVER_BUSY.getCode());
		dataCheckUserBuzy.setString(ChatField.CHANNEL_ID, channelId);
		dataCheckUserBuzy.setString(ChatField.SENDER_NAME, from);
		RPCFuture<PuElement> publishCheck = getContext().getChatProducer().publish(dataCheckUserBuzy);
		try {
			PuObject result = (PuObject) publishCheck.get();
			isReceiverBuzy = result.getBoolean(ChatField.IS_BUSY);
		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
		}
		return isReceiverBuzy;
	}

	/**
	 * get busy users in channel chat with user A
	 */
	public List<String> getUserBusysInChannel(String channelId, String userName) {
		List<String> users = new ArrayList<String>();
		for (String user : getUsersInChannelByChannelId(channelId, userName)) {
			if (isReceiverIsBusy(channelId, userName)) {
				users.add(user);
			}
		}
		return users;
	}

	/**
	 * get offline users in channel chat with user A
	 */
	public List<String> getUserOfflinesInChannel(String channelId, String userName) {
		List<String> userOfflines = new ArrayList<>();
		for (String user : getUsersInChannelByChannelId(channelId, userName)) {
			if (!isUserOnline(user)) {
				userOfflines.add(user);
			}
		}
		return userOfflines;
	}
}
