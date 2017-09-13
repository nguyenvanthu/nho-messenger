package com.nho.server.test.event;

import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.server.entity.user.UserEvent;

public class UserManagerTest extends BaseEventDispatcher {
	public UserManagerTest(){
		this.addEventListener(UserEvent.USER_ONLINE, new UserOnlineHandler());
		this.addEventListener(UserEvent.USER_OFFLINE, new UserOfflineHandler());
	}
	public void whenUserOffline(String sessionId,String userName){
		this.dispatchEvent(UserEvent.createUserOfflineEvent(sessionId, userName));
	}
	public void whenUserOnline(String sessionId,String userName){
		this.dispatchEvent(UserEvent.createUserOnlineEvent(sessionId, userName));
	}
}
