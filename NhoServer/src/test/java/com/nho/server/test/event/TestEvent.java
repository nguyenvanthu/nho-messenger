package com.nho.server.test.event;

public class TestEvent {
	public static void main(String[] args) {
		UserManagerTest test = new UserManagerTest();
		test.whenUserOffline("offline", "thunv");
	}
}
