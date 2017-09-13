package com.nho.notification.router.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.notification.annotation.NotificationCommandProcessor;
import com.nho.notification.data.DeviceTokenMongoBean;
import com.nho.notification.exception.NotificationException;
import com.nho.notification.router.NotificationAbstractProcessor;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.statics.F;

@NotificationCommandProcessor(command = { NotifcationCommand.PUSH_BY_USERNAME })
public class PushByUserNameProcessor extends NotificationAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws NotificationException {
		PuObject data = (PuObject) request;
		data.setType(NotificationField.MESSAGE, PuDataType.STRING);
		data.setType(NotificationField.USERNAME, PuDataType.STRING);
		data.setType(NotificationField.TITLE, PuDataType.STRING);
		String deviceTokenInApp = "";
		if (data.variableExists(NotificationField.DEVICE_TOKEN)) {
			data.setType(NotificationField.DEVICE_TOKEN, PuDataType.STRING);
			deviceTokenInApp = data.getString(NotificationField.DEVICE_TOKEN);
		}

		String message = data.getString(NotificationField.MESSAGE);
		String userName = data.getString(NotificationField.USERNAME);
		String title = data.getString(NotificationField.TITLE);

		List<DeviceTokenMongoBean> deviceTokenBeans = new ArrayList<>();
		deviceTokenBeans = this.getDeviceTokenModel().findByUserName(userName);
		getLogger().debug("send push notification by name in mode " + this.getContext().getModeTest());
		if (this.getContext().getModeTest()) {
			List<String> deviceTokens = new ArrayList<>();
			for (DeviceTokenMongoBean bean : deviceTokenBeans) {
				deviceTokens.add(bean.getDeviceToken());
			}
			getLogger().debug("number deviceTokens " + deviceTokens.size());
			for (String deviceToken : deviceTokens) {
				if (!deviceToken.equals(deviceTokenInApp)) {
					getLogger().debug("send push notifcation to device {} ",deviceToken);
					try {
						this.getPushNotificationManager().pushToGCM(message, title, deviceToken);
					} catch (Exception exception) {
						getLogger().debug("error when push directly " + exception);
					}
				}
			}

		} else {
			try {
				List<String> deviceTokenIds = new ArrayList<>();
				for (DeviceTokenMongoBean bean : deviceTokenBeans) {
					if (!bean.getDeviceToken().equals(deviceTokenInApp)) {
						deviceTokenIds.add(bean.getDeviceTokenId());
					}
				}
				for (String deviceTokenId : deviceTokenIds) {
					getLogger().debug("send push notifcation by hermes");
					this.getPushNotificationManager().pushNotificationByHermes(message, title, deviceTokenId);
				}
			} catch (IOException e) {
				getLogger().debug("error when push notification using hermes");
				e.printStackTrace();
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 0));
	}

}
