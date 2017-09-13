package com.nho.message.response.channel;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.AppState;

public class StateAppChangeResponse extends NhoMessage {
	{
		this.setType(MessageType.STATE_APP_CHANGE_RESPONSE);
	}

	private String userChange;
	private AppState state;

	public String getUserChange() {
		return userChange;
	}

	public void setUserChange(String userChange) {
		this.userChange = userChange;
	}

	public AppState getState() {
		return state;
	}

	public void setState(AppState state) {
		this.state = state;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.userChange);
		puArray.addFrom(this.state.getCode());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.userChange = puArray.remove(0).getString();
		this.state = AppState.fromCode(puArray.remove(0).getInteger());
	}
}
