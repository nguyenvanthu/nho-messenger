package com.nho.vo;

import java.util.Map.Entry;
import java.util.UUID;

import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.statics.F;
import com.nho.statics.Gender;

public class UserProfile {

	// profile infors
	private String id;
	private String name;

	// user infors...
	private int age;
	private int gender;
	private String avatar;
	private String displayName;

	public String getDisplayName() {
		if (this.displayName == null) {
			return "Undefined";
		}
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void autoId() {
		this.setId(UUID.randomUUID().toString());
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Gender getGender() {
		return Gender.fromId(this.gender);
	}

	public void setGender(Gender gender) {
		this.gender = gender.getId();
	}

	public void setGender(int genderId) {
		Gender gender = Gender.fromId(genderId);
		if (gender == null) {
			throw new IllegalArgumentException("Gender id invalid");
		}
		this.gender = genderId;
	}

	public PuObject toBasicInfo() {
		PuObject puo = new PuObject();
		puo.setString(F.ID, this.getId());
		puo.setString(F.NAME, this.getName());
		puo.setString(F.AVATAR, this.getAvatar());
		puo.setString(F.DISPLAY_NAME, this.getDisplayName());
		return puo;
	}

	public PuObject toFullInfo() {
		PuObject puo = this.toBasicInfo();
		puo.setInteger(F.AGE, this.getAge());
		puo.setInteger(F.GENDER, this.gender);
		return puo;
	}

	public PuObject update(PuObject updatingValues) {
		PuObject updatedValues = new PuObject();
		for (Entry<String, PuValue> entry : updatingValues) {
			switch (entry.getKey()) {
			case "displayName":
			case "name":
				String newName = entry.getValue().getString();
				if (!newName.equals(this.displayName)) {
					this.displayName = newName;
					updatedValues.set(entry.getKey(), entry.getValue());
				}
				break;
			}
		}
		return updatedValues;
	}

	public static UserProfile fromPuObject(PuObject puo) {
		if (puo == null) {
			return null;
		}
		UserProfile profile = new UserProfile();
		if (puo.variableExists(F.ID)) {
			profile.setId(puo.getString(F.ID));
		}
		if (puo.variableExists(F.AVATAR)) {
			profile.setAvatar(puo.getString(F.AVATAR));
		}
		if (puo.variableExists(F.DISPLAY_NAME)) {
			profile.setDisplayName(puo.getString(F.DISPLAY_NAME));
		}
		if (puo.variableExists(F.AGE)) {
			profile.setAge(puo.getInteger(F.AGE));
		}
		if (puo.variableExists(F.GENDER)) {
			profile.setGender(puo.getInteger(F.GENDER));
		}
		if (puo.variableExists(F.NAME)) {
			profile.setName(puo.getString(F.NAME));
		}
		return profile;
	}
}
