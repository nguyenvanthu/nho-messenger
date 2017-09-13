package com.nho.message.request.friend;

import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class GetListStatusFriend extends NhoMessage implements Request {
	{
		this.setType(MessageType.GET_LIST_STATUS_FRIEND);
	}
}
