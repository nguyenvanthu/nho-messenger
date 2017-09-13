package com.nho.uams.router.impl;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.uams.annotation.UAMSCommandProcessor;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.message.impl.GetCountLogByActivityAndTimeMessage;
import com.nho.uams.router.UAMSAbstractProcessor;
import com.nho.uams.statics.UAMSApiField;

@UAMSCommandProcessor(command = { UAMSMessageType.GET_COUNT_BY_ACTIVITY_TIME })
public class GetCountLogByActivityAndTimeProcessor extends UAMSAbstractProcessor<GetCountLogByActivityAndTimeMessage> {

	@Override
	protected PuElement process(GetCountLogByActivityAndTimeMessage request) {
		long count = this.getActivityModel().fetchTotalLogByActivityAndTimestamp(request.getActivityType().getCode(),
				request.getStartTime(), request.getEndTime(), request.getApplicationId());
		PuObject result = new PuObject();
		result.setInteger(UAMSApiField.STATUS, 0);
		result.setLong(UAMSApiField.COUNT, count);

		return result;
	}

}
