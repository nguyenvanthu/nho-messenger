package com.nho.server.test.event;

import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.impl.BaseEventHandler;
import com.nho.server.entity.user.UserEvent;

public class UserOnlineHandler extends BaseEventHandler {
	@Override
	public void onEvent(Event event) throws Exception {
		UserEvent userEvent = (UserEvent) event;
		System.out.println("user online is "+userEvent.getUserName());
	}
}
