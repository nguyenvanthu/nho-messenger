package com.nho.server.helper;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.server.NhoServer;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.message.impl.UserActivityMessage;
import com.nho.uams.statics.ActivityType;
import com.nho.uams.statics.UAMSApiField;

public class LoggingHelper extends AbstractHelper {

	public LoggingHelper(NhoServer context) {
		super.setContext(context);
	}

	public void sendMessageLogByRabbit(String messageContent, ActivityType type, String userName, String referenId) {
		getLogger().debug("send log by rabbitmq: " + type);
		PuObject data = new PuObject();
		data.setString(UAMSApiField.MESSAGE_CONTENT, messageContent);
		data.setInteger(UAMSApiField.ACTIVITY_TYPE, type.getCode());
		data.setString(UAMSApiField.APPLICATION_ID, getContext().getApplicationId());
		data.setString(UAMSApiField.USERNAME, userName);
		data.setLong(UAMSApiField.TIME_STAMP, System.currentTimeMillis());
		data.setString(UAMSApiField.REFERENCE_ID, referenId);
		data.setInteger(UAMSApiField.MESSAGE_TYPE, UAMSMessageType.INSERT_USER_ACTIVITY.getId());

		@SuppressWarnings("unused")
		RPCFuture<PuElement> publish = this.getContext().getLoggingProducer().publish(data);
	}

	public void sendMessageLogByKafka(String messageContent, ActivityType type, String userName, String referenId) {
		getLogger().debug("send log by kafka: " + type);
		UserActivityMessage message = new UserActivityMessage();
		message.setActivityType(type.getCode());
		message.setApplicationId(getContext().getApplicationId());
		message.setUsername(userName);
		message.setTimestamp(System.currentTimeMillis());
		message.setRefId(referenId);
		message.setContent(messageContent);
		message.setType(UAMSMessageType.INSERT_USER_ACTIVITY);

		 getContext().getUAMSClient().send(message);
	}
}
