package com.nho.server.statics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatCounter {
	private static Map<String, Long> userToLastPushNotificationTime = new ConcurrentHashMap<>();
	private static Map<String, Long> userToLastPushDrawTime = new ConcurrentHashMap<>();
	private static final long PERIOD_TIME =  60 * 1000L;

	public static boolean isPushNotification(String userName) {
		boolean isPush = false;
		if (!userToLastPushNotificationTime.containsKey(userName)) {
			userToLastPushNotificationTime.put(userName, System.currentTimeMillis());
			isPush = true;
		} else {
			long currentTime = System.currentTimeMillis();
			long lastPushTime = userToLastPushNotificationTime.get(userName);
			if (currentTime - lastPushTime > PERIOD_TIME) {
				isPush = true;
			}
			userToLastPushNotificationTime.put(userName, currentTime);
		}
		return isPush;
	}

	public static void removePushTime(String userName) {
		if (userToLastPushNotificationTime.containsKey(userName)) {
			userToLastPushNotificationTime.remove(userName);
		}
	}
	
	public static boolean isPushNotificationDraw(String userName) {
		boolean isPush = false;
		if (!userToLastPushDrawTime.containsKey(userName)) {
			userToLastPushDrawTime.put(userName, System.currentTimeMillis());
			isPush = true;
		} else {
			long currentTime = System.currentTimeMillis();
			long lastPushTime = userToLastPushDrawTime.get(userName);
			if (currentTime - lastPushTime > PERIOD_TIME) {
				isPush = true;
			}
			userToLastPushNotificationTime.put(userName, currentTime);
		}
		return isPush;
	}

	public static void removePushTimeDraw(String userName) {
		if (userToLastPushDrawTime.containsKey(userName)) {
			userToLastPushDrawTime.remove(userName);
		}
	}
}
