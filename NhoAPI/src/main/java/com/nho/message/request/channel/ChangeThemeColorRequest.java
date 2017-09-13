package com.nho.message.request.channel;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class ChangeThemeColorRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.CHANGE_THEME_COLOR);
	}
	private String user;
	private String backgroundColor = "White";
	private Theme theme = Theme.TEAL;
	private Personality personality = Personality.SERIOUS;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.user);
		puArray.addFrom(this.backgroundColor);
		puArray.addFrom(this.theme.getCode());
		puArray.addFrom(this.personality.getCode());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.user = puArray.remove(0).getString();
		this.backgroundColor = puArray.remove(0).getString();
		this.theme = Theme.fromCode(puArray.remove(0).getInteger());
		this.personality = Personality.fromCode(puArray.remove(0).getInteger());
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
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
