package com.nho.uams.message.impl;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.uams.data.UserActivityBean;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.UAMSApiField;

public class UserActivityMessage extends UAMSAbstractMessage {
	private static final long serialVersionUID = 1L;

	{
		this.setType(UAMSMessageType.INSERT_USER_ACTIVITY);
	}
	private String username;
	private int activityType;
	private String content;
	private String refId;
	private long timestamp;

	public UserActivityBean toActivityLogBean() {
		UserActivityBean bean = new UserActivityBean();
		bean.setActivityType(this.activityType);
		bean.setContent(content);
		bean.setReferenceId(refId);
		bean.setTimestamp(timestamp);
		bean.setUserName(username);

		return bean;
	}

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(username);
		array.addFrom(activityType);
		array.addFrom(content);
		array.addFrom(refId);
		array.addFrom(timestamp);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.username = array.remove(0).getString();
		this.activityType = array.remove(0).getInteger();
		this.content = array.remove(0).getString();
		this.refId = array.remove(0).getString();
		this.timestamp = array.remove(0).getLong();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public static UserActivityMessage fromPuObject(PuObject data) {
		UserActivityMessage message = new UserActivityMessage();
		data.setType(UAMSApiField.MESSAGE_CONTENT, PuDataType.STRING);
		data.setType(UAMSApiField.APPLICATION_ID, PuDataType.STRING);
		data.setType(UAMSApiField.USERNAME, PuDataType.STRING);
		data.setType(UAMSApiField.REFERENCE_ID, PuDataType.STRING);

		message.setActivityType(data.getInteger(UAMSApiField.ACTIVITY_TYPE));
		message.setApplicationId(data.getString(UAMSApiField.APPLICATION_ID));
		message.setContent(data.getString(UAMSApiField.CONTENT));
		message.setRefId(data.getString(UAMSApiField.REFERENCE_ID));
		message.setTimestamp(data.getLong(UAMSApiField.TIME_STAMP));
		message.setUsername(data.getString(UAMSApiField.USERNAME));

		return message;
	}
}
