package com.nho.notification.router.impl;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.notification.annotation.NotificationCommandProcessor;
import com.nho.notification.exception.NotificationException;
import com.nho.notification.router.NotificationAbstractProcessor;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.statics.F;

@NotificationCommandProcessor(command = { NotifcationCommand.REMOVE_TOKEN })
public class RemoveTokenProcessor extends NotificationAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws NotificationException {
		PuObject data = (PuObject) request;
		data.setType(NotificationField.DEVICE_TOKEN, PuDataType.STRING);
		String token = data.getString(NotificationField.DEVICE_TOKEN);

		long deleteCount = this.getDeviceTokenModel().deleteDeviceToken(token);
		getLogger().debug("number delete when delete DeviceToken " + deleteCount);
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 0));
	}

}
