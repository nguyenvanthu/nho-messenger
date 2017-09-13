package com.nho.message.response.channel;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class ChangeThemeColorResponse extends NhoMessage{
	{
		this.setType(MessageType.CHANGE_THEME_COLOR_RESPONSE);
	}
	private String userName ;
	private String channelId ;
	private Theme theme ;
	private Personality personality;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.userName);
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.theme.getCode());
		puArray.addFrom(this.personality.getCode());
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.userName = puArray.remove(0).getString();
		this.channelId = puArray.remove(0).getString();
		this.theme = Theme.fromCode(puArray.remove(0).getInteger());
		this.personality = Personality.fromCode(puArray.remove(0).getInteger());
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	
	
}
