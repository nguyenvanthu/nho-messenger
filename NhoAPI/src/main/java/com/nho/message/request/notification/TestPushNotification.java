package com.nho.message.request.notification;

import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class TestPushNotification extends NhoMessage implements Request{
	{
		this.setType(MessageType.TEST_PUSH);
	}
}
