package com.nho.message.request.login;

import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class ReturnAppRequest extends NhoMessage implements Request{
	{
		this.setType(MessageType.RETURN_APP);
	}
}
