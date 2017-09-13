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
import com.nho.uams.message.impl.FetchByTimeStampMessage;
import com.nho.uams.router.UAMSAbstractProcessor;
import com.nho.uams.statics.UAMSApiField;
import com.nho.uams.statics.UAMSField;

@UAMSCommandProcessor(command = { UAMSMessageType.FETCH_BY_TIMESTAMP })
public class FetchByTimeStampProcessor extends UAMSAbstractProcessor<FetchByTimeStampMessage> {

	@Override
	protected PuElement process(FetchByTimeStampMessage request) {
		getLogger().debug("receive message get log by time stamp");
		Collection<UserActivityBean> beans = this.getActivityModel().fetchLogByTimeStamp(request.getStartTime(),
				request.getEndTime(), request.getApplicationId());
		PuArray userNameArrays = new PuArrayList();
		PuArray activityArrays = new PuArrayList();
		PuArray contents = new PuArrayList();
		PuArray times = new PuArrayList();
		PuArray referenceArrays = new PuArrayList();
		if (beans != null) {
			for (UserActivityBean bean : beans) {
				userNameArrays.addFrom(bean.getUserName());
				activityArrays.addFrom(bean.getActivityType());
				contents.addFrom(bean.getContent());
				times.addFrom(bean.getTimestamp());
				referenceArrays.addFrom(bean.getReferenceId());
			}
			PuObject result = new PuObject();
			result.setInteger(UAMSApiField.STATUS, 0);
			result.setPuArray(UAMSApiField.USERNAMES, userNameArrays);
			result.setPuArray(UAMSApiField.ACTIVITY_TYPES, activityArrays);
			result.setPuArray(UAMSApiField.CONTENTS, contents);
			result.setPuArray(UAMSApiField.TIME_STAMPS, times);
			result.setPuArray(UAMSApiField.REFERENCE_IDS, referenceArrays);
			return result;
		}
		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

}
