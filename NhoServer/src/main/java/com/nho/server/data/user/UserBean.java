package com.nho.server.data.user;

import com.nhb.common.db.beans.UUIDBean;
import com.nhb.common.encrypt.utils.EncryptionUtils;
import com.nhb.common.utils.DateTimeUtils;
import com.nho.server.data.profile.ProfileBean;

public class UserBean extends UUIDBean {

	public static final int SALT_LENGTH = 16;

	private static final long serialVersionUID = 1L;

	private String userName;
	private byte[] password;
	private byte[] salt;
	private boolean disabled;
	private int createdTime;
	private ProfileBean profile;

	public ProfileBean getProfile() {
		return profile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(int createdTime) {
		this.createdTime = createdTime;
	}

	public void autoSalt() {
		this.setSalt(EncryptionUtils.randomBytes(SALT_LENGTH));
	}

	public void autoCreatedTime() {
		this.setCreatedTime(DateTimeUtils.getCurrentUnixTime());
	}
}
