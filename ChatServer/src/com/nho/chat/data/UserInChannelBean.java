package com.nho.chat.data;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.chat.statics.ChannelDBField;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class UserInChannelBean extends AbstractMongoBean{
	private static final long serialVersionUID = 1L;
	private String userName ;
	private Theme theme ;
	private Personality personality;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	@Override
	public Document toDocument() {
		Document document = new Document();
		document.append(ChannelDBField.USER_NAME, this.userName);
		document.append(ChannelDBField.THEME, this.theme.getCode());
		document.append(ChannelDBField.PERSONALITY,this.personality.getCode());
		
		return document;
	}
	
	public static UserInChannelBean fromDocument(Document document){
		UserInChannelBean bean = new UserInChannelBean();
		bean.setUserName(document.getString(ChannelDBField.USER_NAME));
		bean.setPersonality(Personality.fromCode(document.getInteger(ChannelDBField.PERSONALITY)));
		bean.setTheme(Theme.fromCode(document.getInteger(ChannelDBField.THEME)));
		
		return bean;
	}
}
