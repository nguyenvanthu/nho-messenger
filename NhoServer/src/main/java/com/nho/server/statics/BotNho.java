package com.nho.server.statics;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BotNho {

	public static final String USER_NAME = "bot";
	public static final String PASSWORD = "bot123";
	public static final String DISPLAY_NAME = "bot";
	public static final String CHECKSUM = UUID.randomUUID().toString();
	public static final String AVATAR = "icon_bot";
	public static final String STICKER_CHAO = "bot_chao";
	public static final String STICKER_DUNG = "bot_dung";
	public static final String STICKER_SAI = "bot_sai";
	public static final long TIME = 13000L;
	public static final long TIME_BOT = 8000L;
	public static final long TIME_LIMITED = 3000L;
	
	private static Map<String, Integer> interactionOfUsers = new ConcurrentHashMap<>();
	private static Map<String, Integer> interactVgdnToUsers = new ConcurrentHashMap<>();
	private static Map<String, Boolean> userToChatWithBot = new ConcurrentHashMap<>();

	private static int interaction;
	private static int interactVgdn;
	public static boolean isCanChatWithBot = false;

	public static boolean isUserCanChatWithBot(String userName) {
		if (!userToChatWithBot.containsKey(userName)) {
			userToChatWithBot.put(userName, false);
			return false;
		} else {
			return userToChatWithBot.get(userName);
		}
	}

	public static void updateStateUserChatWithBot(String userName, boolean value) {
		userToChatWithBot.put(userName, value);
	}

	public static int incrementInteraction(String userName) {
		if (interactionOfUsers.containsKey(userName)) {
			interaction = interactionOfUsers.get(userName);
			interaction += 1;
			interactionOfUsers.put(userName, interaction);
			return interaction;
		} else {
			interaction = 1;
			interactionOfUsers.put(userName, interaction);
			return interaction;
		}
	}

	public static int incrementInteractVgdn(String userName) {
		if (interactVgdnToUsers.containsKey(userName)) {
			interactVgdn = interactVgdnToUsers.get(userName);
			interactVgdn += 1;
			interactVgdnToUsers.put(userName, interactVgdn);
			return interactVgdn;
		} else {
			interactVgdn = 1;
			interactVgdnToUsers.put(userName, interactVgdn);
			return interactVgdn;
		}
	}

	public static void decrementInteraction(String userName) {
		if (interactionOfUsers.containsKey(userName)) {
			interaction = interactionOfUsers.get(userName);
			interaction -= 1;
			interactionOfUsers.put(userName, interaction);
		}
	}

	public static void decrementInteractVgdn(String userName) {
		if (interactVgdnToUsers.containsKey(userName)) {
			interactVgdn = interactVgdnToUsers.get(userName);
			interactVgdn -= 1;
			interactVgdnToUsers.put(userName, interactVgdn);
		}
	}

	public static void addInteraction(String userName) {
		interaction = 1;
		interactionOfUsers.put(userName, interaction);
	}

	public static void resetInteraction(String userName) {
		interaction = 1;
		interactionOfUsers.put(userName, interaction);
	}

	public static void addInteractVgdn(String userName) {
		interactVgdn = 1;
		interactVgdnToUsers.put(userName, interactVgdn);
	}

	public static void resetInteractVgdn(String userName) {
		interactVgdn = 1;
		interactVgdnToUsers.put(userName, interactVgdn);
	}

	public static void removeInteractionOfUser(String userName) {
		if (interactionOfUsers.containsKey(userName)) {
			interactionOfUsers.remove(userName);
		}
	}

	public static int getInteractionOfUser(String userName) {
		if (interactionOfUsers.containsKey(userName)) {
			return interactionOfUsers.get(userName);
		} else {
			return 0;
		}
	}

	public static void removeInteractVgdn(String userName) {
		if (interactVgdnToUsers.containsKey(userName)) {
			interactVgdnToUsers.remove(userName);
		}
	}

	public static int getInteractVgdnOfUser(String userName) {
		if (interactVgdnToUsers.containsKey(userName)) {
			return interactVgdnToUsers.get(userName);
		} else {
			return 0;
		}
	}
}
