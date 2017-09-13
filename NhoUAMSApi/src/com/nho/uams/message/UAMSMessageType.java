package com.nho.uams.message;

import java.util.concurrent.atomic.AtomicInteger;

import com.nho.uams.message.impl.FetchByActivityAndTimestampMessage;
import com.nho.uams.message.impl.FetchByActivityTypeMessage;
import com.nho.uams.message.impl.FetchByReferenceMessage;
import com.nho.uams.message.impl.FetchByTimeStampMessage;
import com.nho.uams.message.impl.FetchByUserActivityMessage;
import com.nho.uams.message.impl.FetchByUserMessage;
import com.nho.uams.message.impl.GetCountLogByActivityAndTimeMessage;
import com.nho.uams.message.impl.UserActivityMessage;

public enum UAMSMessageType {
	INSERT_USER_ACTIVITY(UserActivityMessage.class),
	FETCH_BY_ACTIVITY(FetchByActivityTypeMessage.class), 
	FETCH_BY_USER(FetchByUserMessage.class), 
	FETCH_BY_REFERENCE(FetchByReferenceMessage.class),
	FETCH_BY_TIMESTAMP(FetchByTimeStampMessage.class),
	FETCH_BY_USER_ACTIVITY(FetchByUserActivityMessage.class), 
	FETCH_BY_ACTIVITY_TIMESTAMP(FetchByActivityAndTimestampMessage.class), 
	GET_COUNT_BY_ACTIVITY_TIME(GetCountLogByActivityAndTimeMessage.class);

	
	private static AtomicInteger idSeed;

	private int genId() {
		if (idSeed == null) {
			idSeed = new AtomicInteger();
		}
		return idSeed.incrementAndGet();
	}

	private int id;
	private Class<? extends UAMSMessage> messageClass;

	private UAMSMessageType(Class<? extends UAMSMessage> messageClass) {
		this.id = genId();
		this.messageClass = messageClass;
	}

	public Class<? extends UAMSMessage> getMessageClass() {
		return this.messageClass;
	}

	public int getId() {
		return this.id;
	}

	public static UAMSMessageType fromCode(int id) {
		for (UAMSMessageType type : values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}

	public static UAMSMessageType forName(String name) {
		for (UAMSMessageType type : values()) {
			String typeName = type.name();
			if (typeName.equalsIgnoreCase(name) || typeName.replace("_", "").equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
