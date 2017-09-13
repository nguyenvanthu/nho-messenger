package com.nho.admin.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.data.PuValue;
import com.nho.admin.annotation.AdminCommandProcessor;
import com.nho.admin.helper.LoggingHelper;
import com.nho.admin.router.AdminAbstractProcessor;
import com.nho.admin.statics.TimeConverter;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.ActivityType;
import com.nho.uams.statics.UAMSApiField;

@AdminCommandProcessor(command = { "userActivity" })
public class GetLogByUserActivityProcessor extends AdminAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) {
		PuObject data = (PuObject) request;
		if (!data.variableExists("userName") || !data.variableExists("type")) {
			getLogger().debug("not enough parameters");
			return PuObject.fromObject(new MapTuple<>("status", 1));
		}
		data.setType("userName", PuDataType.STRING);
		String userName = data.getString("userName", "");
		int activityType = data.getInteger("type", 0);
		String title = "view log activity " + ActivityType.fromCode(activityType) + " of user " + userName;
		LoggingHelper helper = new LoggingHelper(getContext());
		PuObject result = helper.getLogFromUAMS(getMessage(userName, activityType));
		if (result.getInteger(UAMSApiField.STATUS) == 0) {
			getLogger().debug("get log from uams success");
			getLogger().debug("times user do activity : " + result.getPuArray(UAMSApiField.TIME_STAMPS).size());
			PuArray timeArray = result.getPuArray(UAMSApiField.TIME_STAMPS);
			PuArray refereceArray = result.getPuArray(UAMSApiField.REFERENCE_IDS);
			List<Long> times = new ArrayList<>();
			List<String> references = new ArrayList<>();
			for (PuValue value : timeArray) {
				times.add(value.getLong());
			}
			for (PuValue value : refereceArray) {
				references.add(value.getString());
			}
			PuArray detailContents = new PuArrayList();
			for (int i = 0; i < times.size() - 1; i++) {
				String detailContent = "user do activity at " + TimeConverter.getDate(times.get(i)) + " in session: "
						+ references.get(i);
				detailContents.addFrom(detailContent);
			}
			PuObject response = new PuObject();
			response.set(UAMSApiField.STATUS, 0);
			response.setString("title", title);
			response.setInteger("number", result.getPuArray(UAMSApiField.ACTIVITY_TYPES).size());
			response.setPuArray("details", detailContents);
			return response;
		}
		return PuObject.fromObject(new MapTuple<>("status", 1));
	}

	private PuObject getMessage(String userName, int activityType) {
		PuObject data = new PuObject();
		data.setInteger(UAMSApiField.MESSAGE_TYPE, UAMSMessageType.FETCH_BY_USER_ACTIVITY.getId());
		data.setString(UAMSApiField.USERNAME, userName);
		data.setInteger(UAMSApiField.ACTIVITY_TYPE, activityType);
		data.setString(UAMSApiField.APPLICATION_ID, this.getContext().getApplicationId());
		return data;
	}
}
