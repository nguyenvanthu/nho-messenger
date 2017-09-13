package com.nho.server.data.profile;

import java.sql.Date;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nhb.common.utils.DateTimeUtils;
import com.nho.server.statics.DBF;
import com.nho.statics.F;
import com.nho.statics.Gender;

public class ProfileMongoDbBean extends AbstractMongoBean {

	private static final long serialVersionUID = 1L;
	private boolean isDefault;
	private String displayName;
	private Date birthday;
	private int gender = -1;
	private int createdTime;

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

	@Override
	public Document toDocument() {
		Document document = new Document();

		document.put(F.IS_DEFAULT, this.isDefault);
		document.put(F.BIRTHDAY, this.birthday.getTime());
		document.put(F.DISPLAY_NAME, this.displayName);
		document.put(F.GENDER, this.gender);
		document.put(F.CREATED_TIME_PROFILE, this.createdTime);

		return document;
	}

	public static ProfileMongoDbBean fromDocument(Document profileDocument) {
		
		ProfileMongoDbBean bean = new ProfileMongoDbBean();

		bean.setObjectId(profileDocument.getObjectId(DBF._ID));
		bean.setDefault(profileDocument.getBoolean(F.IS_DEFAULT));
		bean.setBirthday(new Date(profileDocument.getLong(F.BIRTHDAY)));
		bean.setGender(Gender.fromId(profileDocument.getInteger(F.GENDER)));
		bean.setDisplayName(profileDocument.getString(F.DISPLAY_NAME));
		bean.setCreatedTime(profileDocument.getInteger(F.CREATED_TIME_PROFILE));

		return bean;
	}

}
