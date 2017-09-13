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
import com.nho.uams.message.impl.FetchByUserMessage;
import com.nho.uams.router.UAMSAbstractProcessor;
import com.nho.uams.statics.UAMSApiField;
import com.nho.uams.statics.UAMSField;

@UAMSCommandProcessor(command = { UAMSMessageType.FETCH_BY_USER })
public class FetchByUserProcessor extends UAMSAbstractProcessor<FetchByUserMessage> {

	@Override
	protected PuElement process(FetchByUserMessage request) {
		getLogger().debug("receive message fetch log by userName");
		Collection<UserActivityBean> beans = this.getActivityModel().fetchLogByUserName(request.getApplicationId(),
				request.getUserName());
		PuArray activityTyeArrays = new PuArrayList();
		PuArray timeArrays = new PuArrayList();
		PuArray contents = new PuArrayList();
		PuArray referenceArrays = new PuArrayList();
		if (beans != null) {
			for (UserActivityBean bean : beans) {
				activityTyeArrays.addFrom(bean.getActivityType());
				timeArrays.addFrom(bean.getTimestamp());
				contents.addFrom(bean.getContent());
				referenceArrays.addFrom(bean.getReferenceId());
			}
			PuObject result = new PuObject();
			result.setInteger(UAMSApiField.STATUS, 0);
			result.setPuArray(UAMSApiField.ACTIVITY_TYPES, activityTyeArrays);
			result.setPuArray(UAMSApiField.TIME_STAMPS, timeArrays);
			result.setPuArray(UAMSApiField.CONTENTS, contents);
			result.setPuArray(UAMSApiField.REFERENCE_IDS, referenceArrays);
			return result;
		}
		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

}
