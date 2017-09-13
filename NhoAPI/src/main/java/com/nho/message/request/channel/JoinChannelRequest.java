package com.nho.message.request.channel;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class JoinChannelRequest extends NhoMessage implements Request {

	{
		this.setType(MessageType.JOIN_CHANNEL);
	}

	private String channelId;
	private Theme theme = Theme.TEAL;
	private Personality personality = Personality.SERIOUS;

	private PuObject message;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.getChannelId());
		puArray.addFrom(this.theme.getCode());
		puArray.addFrom(this.personality.getCode());
		puArray.addFrom(this.getMessage());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.setChannelId(puArray.remove(0).getString());
		this.theme = Theme.fromCode(puArray.remove(0).getInteger());
		this.personality = Personality.fromCode(puArray.remove(0).getInteger());
		this.setMessage(puArray.remove(0).getPuObject());
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public PuObject getMessage() {
		return message;
	}

	public void setMessage(PuObject message) {
		this.message = message;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public Personality getPersonality() {
		return personality;
	}

	public void setPersonality(Personality personality) {
		this.personality = personality;
	}

}
