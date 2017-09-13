package com.nho.message.request.chat;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.ChatMode;

public class ChangeModeChatWithBotRequest extends NhoMessage implements Request{
	{
		this.setType(MessageType.CHANGE_MODE_CHAT);
	}
	private ChatMode mode ;
	public ChatMode getMode() {
		return mode;
	}
	public void setMode(ChatMode mode) {
		this.mode = mode;
	}
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.mode.getCode());
	}
	@Override
	protected void readPuArray(PuArray puArray) {
		this.mode = ChatMode.fromCode(puArray.remove(0).getInteger());
	}

}
