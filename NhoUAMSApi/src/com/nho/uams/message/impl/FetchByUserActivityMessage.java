package com.nho.uams.message.impl;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.ActivityType;
import com.nho.uams.statics.UAMSApiField;

public class FetchByUserActivityMessage extends UAMSAbstractMessage {
	private static final long serialVersionUID = 1L;
	{
		this.setType(UAMSMessageType.FETCH_BY_USER_ACTIVITY);
	}

	private String userName;
	private ActivityType activityType;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(this.userName);
		array.addFrom(this.activityType.getCode());
	}

	@Override
	public void readPuArray(PuArray array) {
		this.userName = array.remove(0).getString();
		this.activityType = ActivityType.fromCode(array.remove(0).getInteger());
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public static FetchByUserActivityMessage fromPuObject(PuObject data) {
		FetchByUserActivityMessage message = new FetchByUserActivityMessage();
		
		data.setType(UAMSApiField.APPLICATION_ID, PuDataType.STRING);
		
		message.setApplicationId(data.getString(UAMSApiField.APPLICATION_ID));
		message.setActivityType(ActivityType.fromCode(data.getInteger(UAMSApiField.ACTIVITY_TYPE)));
		message.setUserName(data.getString(UAMSApiField.USERNAME));
		return message;
	}
}
