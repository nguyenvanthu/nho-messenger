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
import com.nho.uams.message.impl.FetchByReferenceMessage;
import com.nho.uams.router.UAMSAbstractProcessor;
import com.nho.uams.statics.UAMSApiField;
import com.nho.uams.statics.UAMSField;

@UAMSCommandProcessor(command = { UAMSMessageType.FETCH_BY_REFERENCE })
public class FetchByReferenceProcessor extends UAMSAbstractProcessor<FetchByReferenceMessage> {

	@Override
	protected PuElement process(FetchByReferenceMessage request) {
		getLogger().debug("receive message get log by reference ");
		Collection<UserActivityBean> beans = this.getActivityModel().fetchLogByReferenceId(request.getReferenceId(),
				request.getApplicationId());
		PuArray userNameArrays = new PuArrayList();
		PuArray timeArrays = new PuArrayList();
		PuArray contents = new PuArrayList();
		PuArray activityArray = new PuArrayList();
		if (beans != null) {
			for (UserActivityBean bean : beans) {
				userNameArrays.addFrom(bean.getUserName());
				timeArrays.addFrom(bean.getTimestamp());
				contents.addFrom(bean.getContent());
				activityArray.addFrom(bean.getActivityType());
			}
			PuObject result = new PuObject();
			result.setInteger(UAMSApiField.STATUS, 0);
			result.setPuArray(UAMSApiField.USERNAMES, userNameArrays);
			result.setPuArray(UAMSApiField.TIME_STAMPS, timeArrays);
			result.setPuArray(UAMSApiField.CONTENTS, contents);
			result.setPuArray(UAMSApiField.ACTIVITY_TYPES, activityArray);
			return result;
		}
		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

}
