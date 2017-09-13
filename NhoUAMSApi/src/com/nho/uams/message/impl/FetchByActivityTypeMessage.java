package com.nho.uams.message.impl;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.ActivityType;
import com.nho.uams.statics.UAMSApiField;

public class FetchByActivityTypeMessage extends UAMSAbstractMessage {
	private static final long serialVersionUID = 1L;
	{
		this.setType(UAMSMessageType.FETCH_BY_ACTIVITY);
	}
	private ActivityType activityType;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(getActivityType().getCode());
	}

	@Override
	public void readPuArray(PuArray array) {
		this.setActivityType(ActivityType.fromCode(array.remove(0).getInteger()));
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public static FetchByActivityTypeMessage fromPuObject(PuObject data) {
		FetchByActivityTypeMessage message = new FetchByActivityTypeMessage();
		data.setType(UAMSApiField.APPLICATION_ID, PuDataType.STRING);

		message.setActivityType(ActivityType.fromCode(data.getInteger(UAMSApiField.ACTIVITY_TYPE)));
		message.setApplicationId(data.getString(UAMSApiField.APPLICATION_ID));
		return message;
	}
}
