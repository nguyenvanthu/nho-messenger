package com.nho.chat.router.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.data.PuValue;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.data.ChannelMongoBean;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.SORT_FRIEND_BY_TIME })
public class SortFriendByLastTimeProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject result = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		String userName = data.getString(ChatField.USER_NAME);
		PuArray friendArray = data.getPuArray(ChatField.FRIENDS);
		List<String> friends = new ArrayList<>();
		for (PuValue value : friendArray) {
			friends.add(value.getString());
		}
		Map<String, Long> priortyOfFriends = new HashMap<>();
		for (String friend : friends) {
			List<String> users = new ArrayList<>();
			users.add(userName);
			users.add(friend);
			List<ChannelMongoBean> beans = this.getChannelMongoModel().findChannelByListUser(users);
			if (beans.size() > 0) {
				ChannelMongoBean bean = beans.get(0);
				priortyOfFriends.put(friend, Long.valueOf(bean.getLastTime()));
			} else {
				priortyOfFriends.put(friend, 0L);
			}
		}
		priortyOfFriends = sortByTime(priortyOfFriends);
		result.setInteger(ChatField.STATUS, 0);
		PuArray sortedFriend = new PuArrayList();
		for (Entry<String, Long> entry : priortyOfFriends.entrySet()) {
			sortedFriend.addFrom(entry.getKey());
		}
		result.setPuArray(ChatField.FRIENDS, sortedFriend);
		return result;
	}

	private Map<String, Long> sortByTime(Map<String, Long> priortyOfFriends) {
		List<Map.Entry<String, Long>> list = new LinkedList<>(priortyOfFriends.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {

			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return (o1.getValue().compareTo(o2.getValue()));
			}
		});
		Map<String, Long> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, Long> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
