package com.nho.admin.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
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

@AdminCommandProcessor(command = { "activityInTime" })
public class GetLogByActivityAndTimeProcessor extends AdminAbstractProcessor {
	private static final Long TIME_IN_DAY = 86400000L;

	@Override
	public PuElement execute(PuObjectRO request) {
		PuObject data = (PuObject) request;
		if (!data.variableExists("type") || !data.variableExists("time")) {
			getLogger().debug("not enough parameters");
			return PuObject.fromObject(new MapTuple<>("status", 1));
		}
		int type = data.getInteger("type");
		String time = data.getString("time");
		String title = "view log activity " + ActivityType.fromCode(type) + " in time " + time;
		long startTime = convertFromDay(time);
		if (startTime < 0) {
			getLogger().debug("time input invalid");
			return PuObject.fromObject(new MapTuple<>("status", 1));
		}
		long endTime = startTime + TIME_IN_DAY;
		getLogger().debug(title);
		LoggingHelper helper = new LoggingHelper(getContext());
		PuObject result = helper.getLogFromUAMS(getMessage(startTime, endTime, type));
		if (result.getInteger(UAMSApiField.STATUS) == 0) {
			getLogger().debug("get log from uams success");
			getLogger().debug("number user do activity in time: " + result.getPuArray(UAMSApiField.USERNAMES).size());
			PuArray userNameArray = result.getPuArray(UAMSApiField.USERNAMES);
			PuArray contentArray = result.getPuArray(UAMSApiField.CONTENTS);
			PuArray timeArray = result.getPuArray(UAMSApiField.TIME_STAMPS);
			PuArray refereceArray = result.getPuArray(UAMSApiField.REFERENCE_IDS);
			List<String> userNames = new ArrayList<>();
			List<String> contents = new ArrayList<>();
			List<Long> times = new ArrayList<>();
			List<String> references = new ArrayList<>();

			for (PuValue value : userNameArray) {
				userNames.add(value.getString());
			}
			for (PuValue value : contentArray) {
				contents.add(value.getString());
			}
			for (PuValue value : timeArray) {
				times.add(value.getLong());
			}
			for (PuValue value : refereceArray) {
				references.add(value.getString());
			}

			PuArray detailContents = new PuArrayList();
			for (int i = 0; i < userNames.size() - 1; i++) {
				String detailContent = userNames.get(i) + " : " + contents.get(i) + " at "
						+ TimeConverter.getDate(times.get(i)) + " in session: " + references.get(i);
				detailContents.addFrom(detailContent);
			}
			PuObject response = new PuObject();
			response.set(UAMSApiField.STATUS, 0);
			response.setString("title", title);
			response.setInteger("number", result.getPuArray(UAMSApiField.USERNAMES).size());
			response.setPuArray("details", detailContents);
			return response;
		}
		return PuObject.fromObject(new MapTuple<>("status", 1));
	}

	private PuObject getMessage(long startTime, long endTime, int activityType) {
		PuObject data = new PuObject();
		data.setInteger(UAMSApiField.MESSAGE_TYPE, UAMSMessageType.FETCH_BY_ACTIVITY_TIMESTAMP.getId());
		data.setInteger(UAMSApiField.ACTIVITY_TYPE, activityType);
		data.setLong(UAMSApiField.START_TIME, startTime);
		data.setLong(UAMSApiField.END_TIME, endTime);
		data.setString(UAMSApiField.APPLICATION_ID, this.getContext().getApplicationId());
		return data;
	}

	private long convertFromDay(String time) {
		try {
			String[] times = time.split("/");
			int day = Integer.parseInt(times[0]);
			int month = Integer.parseInt(times[1]);
			int year = Integer.parseInt(times[2]);
			return TimeConverter.getTimeOfDate(day, month, year);
		} catch (Exception exception) {
			getLogger().debug("error when convert time");
			return -1;
		}
	}

}
