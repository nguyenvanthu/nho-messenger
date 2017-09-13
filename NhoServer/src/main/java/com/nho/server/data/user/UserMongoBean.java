package com.nho.server.data.user;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.server.data.avatar.AvatarMongoBean;
import com.nho.server.statics.DBF;

public class UserMongoBean extends AbstractMongoBean {
	public static final int SALT_LENGTH = 16;

	private static final long serialVersionUID = 1L;

	private String userName; // facebookId
	private AvatarMongoBean avatar;
	private String displayName;
	private String facebookToken;
	private String email;
	private long lastTimeOnline = 0L;

	public long getLastTimeOnline() {
		return lastTimeOnline;
	}

	public void setLastTimeOnline(long lastTimeOnline) {
		this.lastTimeOnline = lastTimeOnline;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFacebookToken() {
		return facebookToken;
	}

	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public AvatarMongoBean getAvatar() {
		return avatar;
	}

	public void setAvatar(AvatarMongoBean avatar) {
		this.avatar = avatar;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Document toDocument() {
		Document document = new Document();

		document.put(DBF._ID, this.getObjectId());
		document.put(DBF.USERNAME, this.userName);
		document.put(DBF.FACE_TOKEN, this.facebookToken);
		document.put(DBF.EMAIL, this.email);
		document.put(DBF.DISPLAY_NAME, this.displayName);
		document.put(DBF.AVATAR, this.avatar.toDocument());
		document.put(DBF.TIME_ONLINE, this.getLastTimeOnline());
		return document;
	}

	public static UserMongoBean fromDocument(Document document) {
		UserMongoBean bean = new UserMongoBean();

		bean.setObjectId(document.getObjectId(DBF._ID));
		bean.setEmail(document.getString(DBF.EMAIL));
		bean.setUserName(document.getString(DBF.USERNAME));
		bean.setFacebookToken(document.getString(DBF.FACE_TOKEN));
		bean.setDisplayName(document.getString(DBF.DISPLAY_NAME));
		bean.setAvatar(AvatarMongoBean.fromDocunment((Document) document.get(DBF.AVATAR)));
		bean.setLastTimeOnline(document.getLong(DBF.TIME_ONLINE) == null ? 0 : document.getLong(DBF.TIME_ONLINE));
		return bean;
	}
}
