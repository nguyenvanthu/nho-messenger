package com.nho.message.request.channel;

import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class CreateChannelWithBotRequest extends NhoMessage implements Request{
	{
		this.setType(MessageType.CREATE_CHANNEL_WITH_BOT);
	}
}
