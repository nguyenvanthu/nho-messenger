package com.nho.message.response.connection;

import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class DisconnectEvent extends NhoMessage {
	{
		this.setType(MessageType.DISCONNECT_EVENT);
	}
}
