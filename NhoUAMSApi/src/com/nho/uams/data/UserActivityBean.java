package com.nho.uams.data;

import com.nhb.common.data.PuObject;
import com.nho.uams.statics.UAMSApiField;

public class UserActivityBean extends TimeBasedUUIDBean {
	private static final long serialVersionUID = -7183904673384842397L;
	private int activityType;
	private String applicationId;
	private String content;
	private String referenceId;
	private long timestamp;
	private String userName;

	public UserActivityBean() {

	}

	public PuObject toPuObject() {
		PuObject object = new PuObject();
		object.set(UAMSApiField.ACTIVITY_TYPE, activityType);
		object.set(UAMSApiField.APPLICATION_ID, applicationId);
		object.set(UAMSApiField.CONTENT, content);
		object.set(UAMSApiField.REFERENCE_ID, referenceId);
		object.set(UAMSApiField.USERNAME, userName);
		object.set(UAMSApiField.TIMESTAMP, timestamp);
		return object;
	}

	@Override
	public String toString() {
		return this.toPuObject().toString();
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void autoTimestamp() {
		this.timestamp = (long) System.currentTimeMillis();

	}

}
