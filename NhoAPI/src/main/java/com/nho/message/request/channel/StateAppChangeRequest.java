package com.nho.message.request.channel;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.AppState;

public class StateAppChangeRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.STATE_APP_CHANGE);
	}
	private AppState state ;
	private String deviceToken ;
	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public AppState getState() {
		return state;
	}

	public void setState(AppState state) {
		this.state = state;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.state.getCode());
		puArray.addFrom(this.deviceToken);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.state = AppState.fromCode(puArray.remove(0).getInteger());
		this.deviceToken = puArray.remove(0).getString();
	}
	
}
