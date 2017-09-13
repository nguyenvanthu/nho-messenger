package com.nho.uams.data;

public class UserActivity {

	private String username;
	private int activityType;
	private String applicationId;
	private String refId;
	private String content;
	private long timestamp;

	public static class UserActivityBuilder {
		private String username;
		private int activityType;
		private String refId;
		private String content;
		private long timestamp;
		private String applicationId;

		public UserActivityBuilder username(String username) {
			this.username = username;
			return this;
		}

		public UserActivityBuilder activityType(int activityType) {
			this.activityType = activityType;
			return this;
		}

		public UserActivityBuilder refId(String refId) {
			this.refId = refId;
			return this;
		}

		public UserActivityBuilder content(String content) {
			this.content = content;
			return this;
		}

		public UserActivityBuilder timestamp(long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public UserActivityBuilder applicationId(String applicationId) {
			this.applicationId = applicationId;
			return this;
		}

		public UserActivity build() {
			UserActivity activity = new UserActivity();
			activity.setActivityType(this.activityType);
			activity.setContent(this.content);
			activity.setUsername(this.username);
			activity.setRefId(this.refId);
			activity.setTimestamp(this.timestamp);
			activity.setApplicationId(this.applicationId);
			return activity;
		}

	}

	public static UserActivityBuilder newBuilder() {
		return new UserActivityBuilder();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
}
