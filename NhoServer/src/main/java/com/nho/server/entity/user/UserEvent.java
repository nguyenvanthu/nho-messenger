package com.nho.server.entity.user;

import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.impl.AbstractEvent;

public class UserEvent extends AbstractEvent {

	public static final String USER_LOGGED_IN = "userLoggedIn";
	public static final String USER_OFFLINE = "userOffline";
	public static final String USER_LOGGED_OUT = "userLoggedOut";
	public static final String USER_ONLINE = "userOnline";
	public static final String USER_ALREADY_LOGGED_IN_ERROR = "userAlreadyLoggedInError";
	public static final String USER_NOT_LOGGED_IN_ERROR = "userNotLoggedInError";

	public static final String SUBSCRIBED_ON_NEW_CHANNEL = "subscribedOnNewChannel";
	public static final String UNSUBSCRIBED_ON_CHANNEL = "unsubscribedOnChannel";

	public static final String NEW_SESSION_OPENED = "newSessionOpened";
	public static final String SESSION_CLOSED = "sessionClosed";

	public static UserEvent createUserSessionOpenedEvent(String sessionId, String userName) {
		UserEvent userEvent = new UserEvent();
		userEvent.sessionId = sessionId;
		userEvent.userName = userName;
		userEvent.setType(NEW_SESSION_OPENED);
		return userEvent;
	}
	
	public static UserEvent createUserOfflineEvent(String sessionId,String userName){
		UserEvent userEvent = new UserEvent();
		userEvent.sessionId = sessionId;
		userEvent.userName = userName;
		userEvent.setType(USER_OFFLINE);
		return userEvent;
	}
	
	public static UserEvent createUserOnlineEvent(String sessionId,String userName){
		UserEvent userEvent = new UserEvent();
		userEvent.sessionId = sessionId;
		userEvent.userName = userName;
		userEvent.setType(USER_ONLINE);
		
		return userEvent;
	}
	
	public static Event createUserSessionClosedEvent(String sessionId) {
		UserEvent userEvent = new UserEvent();
		userEvent.sessionId = sessionId;
		userEvent.setType(SESSION_CLOSED);
		return userEvent;
	}

	public static UserEvent createUserLoggedOutEvent(User user) {
		UserEvent userEvent = new UserEvent();
		userEvent.user = user;
		userEvent.setType(USER_LOGGED_OUT);
		return userEvent;
	}

	public static Event createUserLoggedInEvent(User user) {
		UserEvent userEvent = new UserEvent();
		userEvent.user = user;
		userEvent.setType(USER_LOGGED_IN);
		return userEvent;
	}

	public static UserEvent createUserAlreadyLoggedInErrorEvent(String userName, String sessionId) {
		UserEvent event = new UserEvent();
		event.setType(USER_ALREADY_LOGGED_IN_ERROR);
		event.userName = userName;
		event.sessionId = sessionId;
		return event;
	}

	public static UserEvent createUserNotLoggedInErrorEvent(String userName, String sessionId) {
		UserEvent event = new UserEvent();
		event.setType(USER_NOT_LOGGED_IN_ERROR);
		event.userName = userName;
		event.sessionId = sessionId;
		return event;
	}

	public static UserEvent createSubscribedOnNewChannelEvent(String channelId) {
		UserEvent userEvent = new UserEvent();
		userEvent.channelId = channelId;
		userEvent.setType(SUBSCRIBED_ON_NEW_CHANNEL);
		return userEvent;
	}

	public static UserEvent createUnsubscribedOnChannelEvent(String channelId) {
		UserEvent userEvent = new UserEvent();
		userEvent.channelId = channelId;
		userEvent.setType(UNSUBSCRIBED_ON_CHANNEL);
		return userEvent;
	}

	private User user;
	private String sessionId;
	private String userName;
	private String channelId;

	public User getUser() {
		return user;
	}

	public String getUserName() {
		if (this.userName == null && this.user != null) {
			return this.user.getUserName();
		}
		return this.userName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void clone(UserEvent other) {

	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
}
