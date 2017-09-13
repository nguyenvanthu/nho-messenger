package com.nho.message.request.channel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.ChannelType;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class CreateChannelRequest extends NhoMessage implements Request {

	{
		super.setType(MessageType.CREATE_CHANNEL);
	}

	private int channelType;
	private PuObject message;
	private Theme theme = Theme.TEAL;
	private Personality personality = Personality.SERIOUS;
	private final Set<String> invitedUsers = new HashSet<>();

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(channelType);
		puArray.addFrom(this.theme.getCode());
		puArray.addFrom(this.personality.getCode());
		puArray.addFrom(this.invitedUsers);
		puArray.addFrom(this.getMessage());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelType = puArray.remove(0).getInteger();
		this.theme = Theme.fromCode(puArray.remove(0).getInteger());
		this.personality = Personality.fromCode(puArray.remove(0).getInteger());
		PuValue val = puArray.remove(0);
		if (val != null && val.getType() == PuDataType.PUARRAY) {
			PuArray arr = val.getPuArray();
			for (PuValue value : arr) {
				this.invitedUsers.add(value.getString());
			}
		}
		this.setMessage(puArray.remove(0).getPuObject());
	}

	public ChannelType getChannelType() {
		return ChannelType.fromCode(this.channelType);
	}

	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType.getCode();
	}

	public void addInvitedUsers(String... users) {
		this.invitedUsers.addAll(Arrays.asList(users));
	}

	public void addInvitedUsers(Collection<String> users) {
		this.invitedUsers.addAll(users);
	}

	public Set<String> getInvitedUsers() {
		return this.invitedUsers;
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
