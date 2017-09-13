package com.nho.server.entity.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;
import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.message.request.chat.ChatMessage;
import com.nho.server.NhoServer;
import com.nho.server.event.UserOfflineHandler;
import com.nho.statics.ChatMode;
import com.nho.statics.StatusUser;

public class UserManager extends BaseEventDispatcher
		implements EntryRemovedListener<String, User>, EntryAddedListener<String, User> {
	public static final String USER_MAP_KEY = "nho:users";

	private final HazelcastInstance hazelcast;

	private NhoServer context;

	public NhoServer getContext() {
		return context;
	}

	public void setContext(NhoServer context) {
		this.context = context;
	}

	/**
	 * users store users in
	 */
	private final Map<String, User> users = new ConcurrentHashMap<>();
	/**
	 * sessionToUserName store session - userName
	 */
	private final Map<String, String> sessionToUserName = new ConcurrentHashMap<>();
	/**
	 * offlineMessages store userName - message chats
	 */
	private final Map<String, List<ChatMessage>> offlineMessages = new ConcurrentHashMap<>();
	/**
	 * sessionToDeviceTokenInApps store session - deviceToken in application
	 */
	private final Map<String, String> sessionToDeviceTokenInApps = new ConcurrentHashMap<>();
	/**
	 * aloneUserToChatMode store user is talking with boot with current chat
	 * mode
	 */
	private Map<String, ChatMode> aloneUserToChatMode = new ConcurrentHashMap<>();
	/**
	 * userInChannelToPushed store map user - channelId : user can pushed
	 * notification
	 */
	private Map<String, String> userInChannelToPushed = new ConcurrentHashMap<>();
	/**
	 * userOnlines store users is online
	 */
	// private Set<String> userOnlines = new CopyOnWriteArraySet<>();
	/**
	 * userLogOuts store users was logout
	 */
	// private Set<String> userLogOuts = new CopyOnWriteArraySet<>();

	private final EventHandler userSessionOpenedListener = new EventHandler() {

		@Override
		public void onEvent(Event event) throws Exception {
			UserEvent userEvent = (UserEvent) event;
			sessionToUserName.put(userEvent.getSessionId(), userEvent.getUserName());
			dispatchEvent(event);
		}
	};

	private final EventHandler userSessionClosedListener = new EventHandler() {

		@Override
		public void onEvent(Event event) throws Exception {
			UserEvent userEvent = (UserEvent) event;
			sessionToUserName.remove(userEvent.getSessionId());
			dispatchEvent(event);
		}
	};

	public boolean isUserOnline(String userName) {
		boolean isOnline = false;
		User user = this.users.get(userName);
		if ( user.getStatus() == StatusUser.ONLINE) {
			isOnline = true;
		}
		return isOnline;
	}

	public boolean isUserOffline(String userName) {
		boolean isOffline = false;
		User user = this.users.get(userName);
		if (user.getStatus() == StatusUser.OFFLINE) {
			isOffline = true;
		}
		return isOffline;
	}
	public StatusUser getStatusUser(String userName){
		User user = this.users.get(userName);
		return user.getStatus();
	}
	public boolean isUserLogout(String userName) {
		boolean isLogout = false;
		User user = this.users.get(userName);
		if (user.getStatus() == StatusUser.LOGOUT) {
			isLogout = true;
		}
		return isLogout;
	}

	public boolean isUserInChannelPushed(String userName, String channelId) {
		boolean isPushed = false;
		if (this.userInChannelToPushed.get(userName).equals(channelId)) {
			isPushed = true;
		}
		return isPushed;
	}

	public void addUserInChannelPushed(String userName, String channelId) {
		if (!this.userInChannelToPushed.containsKey(userName)) {
			this.userInChannelToPushed.put(userName, channelId);
		}
	}

	public void removeUserInChannelPushed(String userName, String channelId) {
		if (this.userInChannelToPushed.containsKey(userName)) {
			if (this.userInChannelToPushed.get(userName).equals(channelId)) {
				this.userInChannelToPushed.remove(userName);
			}
		}
	}

	public UserManager(NhoServer context, HazelcastInstance hazelcast) {
		this.hazelcast = hazelcast;
		if (this.hazelcast == null) {
			throw new IllegalArgumentException("HazelcastInsance is required for UserManager to work");
		}
		this.context = context;
		this.addEventListener(UserEvent.USER_OFFLINE, new UserOfflineHandler(this.getContext()));
	}

	public List<ChatMessage> getListMessageOfflineOfUser(String user) {
		try {
			List<ChatMessage> messageOfflines = new ArrayList<ChatMessage>();
			for (String userOffline : this.offlineMessages.keySet()) {
				if (user.equals(userOffline)) {
					messageOfflines.addAll(this.offlineMessages.get(userOffline));
					this.offlineMessages.remove(userOffline);
				}
			}
			getLogger().debug("list message Offlines " + messageOfflines.size());
			return messageOfflines;
		} catch (Exception ex) {
			getLogger().debug(ex.getMessage());
			return null;
		}
	}

	public Map<String, List<ChatMessage>> getMessageOffline() {
		return this.offlineMessages;
	}

	public User getUserByUserName(String userName) {
		if (userName == null) {
			return null;
		}
		return this.users.get(userName);
	}

	public User getUserBySessionId(String sessionId) {
		return this.getUserByUserName(this.sessionToUserName.get(sessionId));
	}

	public User removeUserSession(String sessionId) {
		User user = this.getUserBySessionId(sessionId);
		if (user != null) {
			user.removeSession(sessionId);
		}
		return user;
	}

	public User addUserIfNotExists(String userName) {
		User user = null;
		if (!this.users.containsKey(userName)) {
			user = new User(userName, this.hazelcast.getSet(String.format("nho:user:{}:subscribedChannels", userName)));
			user.setStatus(StatusUser.ONLINE);
			user.addEventListener(UserEvent.NEW_SESSION_OPENED, this.userSessionOpenedListener);
			user.addEventListener(UserEvent.SESSION_CLOSED, this.userSessionClosedListener);
			return user;
		} else {
			user = this.users.get(userName);
			user.setStatus(StatusUser.ONLINE);
		}
		// this.addNewUser(user);
		return user;
	}

	public void addNewUser(User user) {
		getLogger().debug("add user {} with status {}", user.getUserName(),user.getStatus());
		this.users.put(user.getUserName(), user);
	}

	// public boolean isUserLoggedIn(String userName) {
	// return this.users.containsKey(userName);
	// }

	public boolean isDeviceInApp(String sessionId) {
		boolean isPaused = false;
		if (this.sessionToDeviceTokenInApps.containsKey(sessionId)) {
			isPaused = true;
		}
		return isPaused;
	}

	public String getDeviceTokenBySessionId(String sessionId) {
		if (this.sessionToDeviceTokenInApps.containsKey(sessionId)) {
			return this.sessionToDeviceTokenInApps.get(sessionId);
		}
		return "";
	}

	public List<String> getListDeviceInApp(String userName) {
		List<String> devicePauseds = new ArrayList<>();
		User user = this.getUserByUserName(userName);
		for (String sessionId : user.getSessions()) {
			if (this.sessionToDeviceTokenInApps.containsKey(sessionId)) {
				devicePauseds.add(this.sessionToDeviceTokenInApps.get(sessionId));
			}
		}

		return devicePauseds;
	}

	public void addNewDeviceInApp(String sessionId, String deviceToken) {
		this.sessionToDeviceTokenInApps.put(sessionId, deviceToken);
	}

	public void removeDeviceInApp(String sessionId) {
		if (this.sessionToDeviceTokenInApps.containsKey(sessionId)) {
			this.sessionToDeviceTokenInApps.remove(sessionId);
		}
	}

	public void removeSessionOfUser(String userName) {
		User user = this.getUserByUserName(userName);
		for (String sessionId : user.getSessions()) {
			if (this.sessionToDeviceTokenInApps.containsKey(sessionId)) {
				this.sessionToDeviceTokenInApps.remove(sessionId);
			}
		}
	}

	public void addUserChatWithBot(String userName, ChatMode mode) {
		getLogger().debug("add user {} to list wait tutorial", userName);
		this.aloneUserToChatMode.put(userName, mode);
	}

	public void removeUserChatWithBot(String userName) {
		if (this.aloneUserToChatMode.containsKey(userName)) {
			this.aloneUserToChatMode.remove(userName);
		}
	}

	public Set<String> getUserChatWithBots() {
		return this.aloneUserToChatMode.keySet();
	}

	public boolean isChatWithBot(String userName) {
		if (this.aloneUserToChatMode.containsKey(userName)) {
			return true;
		}
		return false;
	}

	public ChatMode getModeChatOfUser(String userName) {
		if (this.aloneUserToChatMode.containsKey(userName)) {
			return this.aloneUserToChatMode.get(userName);
		}
		return null;
	}

	public void whenUserDisconect(String userName, String sessionId) {
		userOffline(userName, sessionId);
		this.dispatchEvent(UserEvent.createUserOfflineEvent(sessionId, userName));
	}

	public void whenUserLogout(String userName, String sessionId) {
		userLogout(userName, sessionId);
		this.dispatchEvent(UserEvent.createUserOfflineEvent(sessionId, userName));
	}

	public void userOffline(String userName, String sessionId) {
		removeUserChatWithBot(userName);
		updateStatusUser(userName, StatusUser.OFFLINE);
		removeDeviceInApp(sessionId);
	}

	public void userOnline(String userName) {
		updateStatusUser(userName, StatusUser.ONLINE);
	}

	public void userLogout(String userName, String sessionId) {
		removeUserChatWithBot(userName);
		updateStatusUser(userName, StatusUser.LOGOUT);
		removeDeviceInApp(sessionId);
	}

	public void updateStatusUser(String userName, StatusUser status) {
		User user = this.users.get(userName);
		user.setStatus(status);
		this.users.put(userName, user);
	}

	@Override
	@Deprecated
	public void entryRemoved(EntryEvent<String, User> event) {

	}

	@Override
	@Deprecated
	public void entryAdded(EntryEvent<String, User> event) {

	}
}
