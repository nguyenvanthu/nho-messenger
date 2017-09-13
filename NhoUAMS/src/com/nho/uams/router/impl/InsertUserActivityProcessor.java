package com.nho.uams.router.impl;

import java.util.UUID;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.uams.annotation.UAMSCommandProcessor;
import com.nho.uams.data.UserActivityBean;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.message.impl.UserActivityMessage;
import com.nho.uams.router.UAMSAbstractProcessor;
import com.nho.uams.statics.UAMSField;

@UAMSCommandProcessor(command = { UAMSMessageType.INSERT_USER_ACTIVITY })
public class InsertUserActivityProcessor extends UAMSAbstractProcessor<UserActivityMessage> {

	@Override
	public PuElement process(UserActivityMessage request) {
		getLogger().debug("process insert user activity message");
		UserActivityBean bean = new UserActivityBean();
		bean.setActivityType(request.getActivityType());
		bean.setApplicationId(request.getApplicationId());
		bean.setContent(request.getContent());
		bean.setReferenceId(request.getRefId());
		bean.setTimestamp(request.getTimestamp());
		bean.setUserName(request.getUsername());
		bean.setId(UUID.randomUUID());
		
		boolean isSuccess = this.getActivityModel().insert(bean);
		if(isSuccess){
			getLogger().debug("insert log to cassandra success");
		}else{
			getLogger().debug("insert fail");
		}
		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

}
