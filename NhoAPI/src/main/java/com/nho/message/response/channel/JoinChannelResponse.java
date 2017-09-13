package com.nho.message.response.channel;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.Personality;
import com.nho.statics.StatusUserInChannel;
import com.nho.statics.Theme;

public class JoinChannelResponse extends NhoMessage {
	{
		this.setType(MessageType.JOIN_CHANNEL_RESPONSE);
	}
	private boolean successful;
	private String channelId;
	private String userInviteChat;
	private StatusUserInChannel statusChannel;
	private Theme theme;
	private Personality personality;
	private Error error;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.channelId);
			puArray.addFrom(this.getUserInviteChat());
			puArray.addFrom(this.theme.getCode());
			puArray.addFrom(this.personality.getCode());
			puArray.addFrom(this.statusChannel.getCode());
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			this.channelId = puArray.remove(0).getString();
			this.setUserInviteChat(puArray.remove(0).getString());
			this.theme = Theme.fromCode(puArray.remove(0).getInteger());
			this.personality = Personality.fromCode(puArray.remove(0).getInteger());
			this.statusChannel = StatusUserInChannel.fromCode(puArray.remove(0).getInteger());
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
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

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public String getUserInviteChat() {
		return userInviteChat;
	}

	public void setUserInviteChat(String userInviteChat) {
		this.userInviteChat = userInviteChat;
	}

	public StatusUserInChannel getStatusChannel() {
		return statusChannel;
	}

	public void setStatusChannel(StatusUserInChannel statusChannel) {
		this.statusChannel = statusChannel;
	}
}
