package com.nho.message.request.chat;

import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class RecognizeRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.RECOGNIZE_REQUEST);
	}
	
}
