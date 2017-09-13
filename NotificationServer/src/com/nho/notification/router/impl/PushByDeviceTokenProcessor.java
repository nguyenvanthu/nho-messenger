package com.nho.notification.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.data.PuValue;
import com.nho.notification.annotation.NotificationCommandProcessor;
import com.nho.notification.exception.NotificationException;
import com.nho.notification.router.NotificationAbstractProcessor;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.statics.F;

@NotificationCommandProcessor(command = { NotifcationCommand.PUSH_BY_DEVICE_TOKEN })
public class PushByDeviceTokenProcessor extends NotificationAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws NotificationException {
		PuObject data = (PuObject) request;
		data.setType(NotificationField.MESSAGE, PuDataType.STRING);
		data.setType(NotificationField.TITLE, PuDataType.STRING);

		String message = data.getString(NotificationField.MESSAGE);
		String title = data.getString(NotificationField.TITLE);

		PuArray array = data.getPuArray(NotificationField.DEVICE_TOKEN);
		List<String> tokens = new ArrayList<String>();
		for (PuValue value : array) {
			tokens.add(value.getString());
		}
		for (String token : tokens) {
			try {
				this.getPushNotificationManager().pushToGCM(message, title, token);
			} catch (Exception exception) {
				getLogger().debug("error when push directly " + exception);
				return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
			}
			getLogger().debug("push notification to device " + token);
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 0));
	}

}
