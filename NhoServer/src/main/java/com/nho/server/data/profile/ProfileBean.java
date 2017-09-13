package com.nho.server.data.profile;

import java.sql.Date;

import com.nhb.common.db.beans.UUIDBean;
import com.nhb.common.utils.DateTimeUtils;
import com.nho.statics.Gender;

public class ProfileBean extends UUIDBean {

	private static final long serialVersionUID = 1L;

	private byte[] userId;
	private boolean isDefault;
	private String displayName;
	private Date birthday;
	private int gender = -1;
	private int createdTime;

	public byte[] getUserId() {
		return userId;
	}

	public void setUserId(byte[] userId) {
		this.userId = userId;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getGenderValue() {
		return this.gender;
	}

	public void setGenderValue(int gender) {
		this.gender = gender;
	}

	public Gender getGender() {
		return Gender.fromId(this.gender);
	}

	public void setGender(Gender gender) {
		this.gender = gender.getId();
	}

	public int getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(int createdTime) {
		this.createdTime = createdTime;
	}

	public void autoCreatedTime() {
		this.setCreatedTime(DateTimeUtils.getCurrentUnixTime());
	}
}
