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
import com.nho.uams.message.impl.FetchByUserActivityMessage;
import com.nho.uams.router.UAMSAbstractProcessor;
import com.nho.uams.statics.UAMSApiField;
import com.nho.uams.statics.UAMSField;

@UAMSCommandProcessor(command = { UAMSMessageType.FETCH_BY_USER_ACTIVITY })
public class FetchByUserActivityProcessor extends UAMSAbstractProcessor<FetchByUserActivityMessage> {

	@Override
	protected PuElement process(FetchByUserActivityMessage request) {
		getLogger().debug("receive message get log by user activity");
		Collection<UserActivityBean> beans = this.getActivityModel().fetchLogByUserActivity(request.getUserName(),
				request.getActivityType().getCode(), request.getApplicationId());
		PuArray timeArrays = new PuArrayList();
		PuArray referenceArrays = new PuArrayList();
		if (beans != null) {
			for (UserActivityBean bean : beans) {
				timeArrays.addFrom(bean.getTimestamp());
				referenceArrays.addFrom(bean.getReferenceId());
			}
			PuObject result = new PuObject();
			result.setInteger(UAMSApiField.STATUS, 0);
			result.setPuArray(UAMSApiField.TIME_STAMPS, timeArrays);
			result.setPuArray(UAMSApiField.REFERENCE_IDS, referenceArrays);
			return result;
		}
		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

}
