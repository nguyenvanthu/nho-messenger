package com.nho.uams;

import com.nhb.common.data.PuObject;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.message.impl.FetchByActivityAndTimestampMessage;
import com.nho.uams.message.impl.FetchByActivityTypeMessage;
import com.nho.uams.message.impl.FetchByReferenceMessage;
import com.nho.uams.message.impl.FetchByTimeStampMessage;
import com.nho.uams.message.impl.FetchByUserActivityMessage;
import com.nho.uams.message.impl.FetchByUserMessage;
import com.nho.uams.message.impl.GetCountLogByActivityAndTimeMessage;
import com.nho.uams.message.impl.UserActivityMessage;
import com.nho.uams.statics.UAMSApiField;

public class MessageConverter {
	public static UAMSAbstractMessage convertFromPuObject(PuObject data) {
		if (data.variableExists(UAMSApiField.MESSAGE_TYPE)) {
			UAMSMessageType type = UAMSMessageType.fromCode(data.getInteger(UAMSApiField.MESSAGE_TYPE));
			switch (type) {
			case INSERT_USER_ACTIVITY:
				return UserActivityMessage.fromPuObject(data);
			case FETCH_BY_ACTIVITY:
				return FetchByActivityTypeMessage.fromPuObject(data);
			case FETCH_BY_REFERENCE:
				return FetchByReferenceMessage.fromPuObject(data);
			case FETCH_BY_TIMESTAMP:
				return FetchByTimeStampMessage.fromPuObject(data);
			case FETCH_BY_USER:
				return FetchByUserMessage.fromPuObject(data);
			case FETCH_BY_USER_ACTIVITY:
				return FetchByUserActivityMessage.fromPuObject(data);
			case FETCH_BY_ACTIVITY_TIMESTAMP:
				return FetchByActivityAndTimestampMessage.fromPuObject(data);
			case GET_COUNT_BY_ACTIVITY_TIME:
				return GetCountLogByActivityAndTimeMessage.fromPuObject(data);
			default:
				break;
			}
		}
		return null;
	}
}
