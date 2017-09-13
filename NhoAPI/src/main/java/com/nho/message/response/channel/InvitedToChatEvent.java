package com.nho.message.response.channel;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class InvitedToChatEvent extends NhoMessage {

	{
		this.setType(MessageType.INVITED_TO_CHAT_EVENT);
	}

	private String sender;
	private String senderDisplayName;
	private String channelId;
	private Theme theme = Theme.TEAL;
	private Personality personality = Personality.SERIOUS;
	private PuObject message;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.sender);
		puArray.addFrom(this.senderDisplayName);
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.theme.getCode());
		puArray.addFrom(this.personality.getCode());
		puArray.addFrom(this.message);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.sender = puArray.remove(0).getString();
		this.senderDisplayName = puArray.remove(0).getString();
		this.channelId = puArray.remove(0).getString();
		this.theme = Theme.fromCode(puArray.remove(0).getInteger());
		this.personality = Personality.fromCode(puArray.remove(0).getInteger());
		this.message = puArray.remove(0).getPuObject();
	}


	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSenderDisplayName() {
		return senderDisplayName;
	}

	public void setSenderDisplayName(String senderDisplayName) {
		this.senderDisplayName = senderDisplayName;
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
