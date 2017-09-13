package com.nho.uams.router.impl;

import java.util.Collection;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.uams.annotation.UAMSCommandProcessor;
import com.nho.uams.data.UserActivityBean;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.message.impl.FetchByActivityAndTimestampMessage;
import com.nho.uams.router.UAMSAbstractProcessor;
import com.nho.uams.statics.UAMSApiField;
import com.nho.uams.statics.UAMSField;

@UAMSCommandProcessor(command = { UAMSMessageType.FETCH_BY_ACTIVITY_TIMESTAMP })
public class FetchByActivityTypeAndTimestampProcessor
		extends UAMSAbstractProcessor<FetchByActivityAndTimestampMessage> {

	@Override
	protected PuElement process(FetchByActivityAndTimestampMessage request) {
		int activityType = request.getActivityType().getCode();
		long startTime = request.getStartTime();
		long endTime = request.getEndTime();
		String applicationId = request.getApplicationId();
		Collection<UserActivityBean> beans = this.getActivityModel().fetchLogByActivityAndTimestamp(activityType, startTime, endTime, applicationId);
		PuArray userNameArrays = new PuArrayList();
		PuArray timeArrays = new PuArrayList();
		PuArray contents = new PuArrayList();
		PuArray referenceArrays = new PuArrayList();
		if(beans != null){
			for(UserActivityBean bean : beans){
				userNameArrays.addFrom(bean.getUserName());
				timeArrays.addFrom(bean.getTimestamp());
				contents.addFrom(bean.getContent());
				referenceArrays.addFrom(bean.getReferenceId());
			}
			PuObject result = new PuObject();
			result.setInteger(UAMSApiField.STATUS, 0);
			result.setPuArray(UAMSApiField.USERNAMES, userNameArrays);
			result.setPuArray(UAMSApiField.TIME_STAMPS, timeArrays);
			result.setPuArray(UAMSApiField.CONTENTS, contents);
			result.setPuArray(UAMSApiField.REFERENCE_IDS, referenceArrays);
			return result;
		}
		
		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

}
