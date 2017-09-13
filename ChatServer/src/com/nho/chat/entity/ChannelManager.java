package com.nho.chat.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.chat.data.ChannelMongoBean;
import com.nho.chat.data.UserInChannelBean;

public class ChannelManager extends BaseEventDispatcher {
	/**
	 * channIdToUsers store map channelId - userNames in channel
	 */
	private Map<String, List<String>> channelIdToUsers = new ConcurrentHashMap<>();

	public boolean isUserBusy(String channelId, String userName) {
		synchronized (channelIdToUsers) {
			boolean isBusy = true;
			for (Entry<String, List<String>> entry : channelIdToUsers.entrySet()) {
				if (entry.getKey().equals(channelId)) {
					if (entry.getValue().contains(userName)) {
						isBusy = false;
					}
				}
			}
			return isBusy;
		}
	}

	public List<String> getChannelIdByUserName(String userName) {
		synchronized (channelIdToUsers) {
			List<String> channels = new ArrayList<>();
			for (Entry<String, List<String>> entry : channelIdToUsers.entrySet()) {
				if (entry.getValue().contains(userName)) {
					getLogger().debug("find channel {}", entry.getKey());
					channels.add(entry.getKey());
				}
			}
			return channels;
		}
	}

	public void addUserInChannel(String channelId, String userName) {
		synchronized (channelIdToUsers) {
			getLogger().debug("add user {} to channel {}", userName, channelId);
			if (this.channelIdToUsers.containsKey(channelId)) {
				List<String> users = this.channelIdToUsers.get(channelId);
				if (!users.contains(userName)) {
					users.add(userName);
					this.channelIdToUsers.put(channelId, users);
				}
			} else {
				List<String> users = new ArrayList<>();
				users.add(userName);
				this.channelIdToUsers.put(channelId, users);
			}
		}
	}

	public void removeUserInChannel(String userName) {
		synchronized (channelIdToUsers) {
			for (Entry<String, List<String>> entry : this.channelIdToUsers.entrySet()) {
				if (entry.getValue().contains(userName)) {
					entry.getValue().remove(userName);
					getLogger().debug("remove user {} in channel {}", userName, entry.getKey());
				}
			}
		}
	}

	public List<String> getUsersInSideChannel(String channelId) {
		synchronized (channelIdToUsers) {
			List<String> users = new ArrayList<>();
			for (Entry<String, List<String>> entry : this.channelIdToUsers.entrySet()) {
				if (entry.getKey().equals(channelId)) {
					users.addAll(entry.getValue());
				}
			}

			return users;
		}
	}

	public List<String> getUserInChannels(ChannelMongoBean channel) {
		List<String> userNames = new ArrayList<>();
		for (UserInChannelBean bean : channel.getUsers()) {
			userNames.add(bean.getUserName());
		}
		return userNames;
	}

}
