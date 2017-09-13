package com.nho.server.entity.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiveObject {
	private static Map<String, Integer> userInChannelToIndexs = new ConcurrentHashMap<>();

	private static int index;

	public static int getAndIncrementIndex(String userName) {
		if (userInChannelToIndexs.containsKey(userName)) {
			index = userInChannelToIndexs.get(userName);
			index += 1;
			userInChannelToIndexs.put(userName, index);
			return index;
		} else {
			index = 1;
			userInChannelToIndexs.put(userName, index);
			return index;
		}
	}
	public static void ressetIndex(String userName){
		index = 1;
		userInChannelToIndexs.put(userName, index);
	}
	public static void decrementIndex(String userName) {
		if (userInChannelToIndexs.containsKey(userName)) {
			index = userInChannelToIndexs.get(userName);
			index -= 1;
			userInChannelToIndexs.put(userName, index);
		}
	}

	public static void addIndex(String userName) {
		index = 1;
		userInChannelToIndexs.put(userName, index);
	}

	public static void removeIndexByUser(String userName) {
		if (userInChannelToIndexs.containsKey(userName)) {
			userInChannelToIndexs.remove(userName);
		}
	}

	public static int getIndexByUser(String userName) {
		if (userInChannelToIndexs.containsKey(userName)) {
			return userInChannelToIndexs.get(userName);
		} else {
			return 0;
		}
	}
}
