package com.nho.message.response.channel;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class JoinedToChannelResponse extends NhoMessage {

	{
		this.setType(MessageType.JOINED_TO_CHANNEL_RESPONSE);
	}

	private boolean successful;
	private String userJoin;
	private String channelId;
	private Theme theme;
	private Personality personality;
	private Error error;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.channelId);
			puArray.addFrom(this.userJoin);
			puArray.addFrom(this.theme.getCode());
			puArray.addFrom(this.personality.getCode());
		} else {
			puArray.addFrom(this.getError() == null ? null : this.error.getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			this.channelId = puArray.remove(0).getString();
			this.userJoin = puArray.remove(0).getString();
			this.theme = Theme.fromCode(puArray.remove(0).getInteger());
			this.personality = Personality.fromCode(puArray.remove(0).getInteger());
		} else {
			PuValue errorValue = puArray.remove(0);
			if (errorValue != null && errorValue.getType() != PuDataType.NULL) {
				this.error = Error.fromCode(errorValue.getInteger());
			}
		}
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
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

	public String getUserJoin() {
		return userJoin;
	}

	public void setUserJoin(String userJoin) {
		this.userJoin = userJoin;
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
