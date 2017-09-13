package com.nho.message.request.chat;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class GetMessageOfflines extends NhoMessage implements Request{
	{
		this.setType(MessageType.GET_MESSAGE_OFFLINE);
	}
	
	private String userName ;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.userName);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.userName = puArray.remove(0).getString();
	}
	
}
