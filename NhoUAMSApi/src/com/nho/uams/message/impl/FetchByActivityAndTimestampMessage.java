package com.nho.uams.message.impl;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.ActivityType;
import com.nho.uams.statics.UAMSApiField;

public class FetchByActivityAndTimestampMessage extends UAMSAbstractMessage {
	private static final long serialVersionUID = 1L;
	{
		this.setType(UAMSMessageType.FETCH_BY_ACTIVITY_TIMESTAMP);
	}
	private ActivityType activityType;
	private long startTime;
	private long endTime;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(this.activityType.getCode());
		array.addFrom(this.startTime);
		array.addFrom(this.endTime);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.activityType = ActivityType.fromCode(array.remove(0).getInteger());
		this.startTime = array.remove(0).getLong();
		this.endTime = array.remove(0).getLong();
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public static FetchByActivityAndTimestampMessage fromPuObject(PuObject data) {
		FetchByActivityAndTimestampMessage message = new FetchByActivityAndTimestampMessage();
		data.setType(UAMSApiField.APPLICATION_ID, PuDataType.STRING);

		message.setActivityType(ActivityType.fromCode(data.getInteger(UAMSApiField.ACTIVITY_TYPE)));
		message.setApplicationId(data.getString(UAMSApiField.APPLICATION_ID));
		message.setStartTime(data.getLong(UAMSApiField.START_TIME));
		message.setEndTime(data.getLong(UAMSApiField.END_TIME));
		
		return message;
	}
}
