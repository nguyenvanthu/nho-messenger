package com.nho.notification.router.impl;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.notification.annotation.NotificationCommandProcessor;
import com.nho.notification.data.DeviceTokenMongoBean;
import com.nho.notification.entity.PushNotificationManager;
import com.nho.notification.exception.NotificationException;
import com.nho.notification.router.NotificationAbstractProcessor;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.notification.statics.PushNotificationConfig;
import com.nho.statics.Error;

@NotificationCommandProcessor(command = { NotifcationCommand.REGISTER_TOKEN })

public class RegisterTokenProcessor extends NotificationAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws NotificationException {
		PuObject result = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(NotificationField.USERNAME, PuDataType.STRING);
		data.setType(NotificationField.DEVICE_TOKEN, PuDataType.STRING);

		String user = data.getString(NotificationField.USERNAME);
		String token = data.getString(NotificationField.DEVICE_TOKEN);

		String userName = this.getDeviceTokenModel().findUserNameByDeviceToken(token);
		if (userName != null) { //update
			DeviceTokenMongoBean deviceTokenBean = this.getDeviceTokenModel().findByToken(token);
			result.setInteger(NotificationField.STATUS, 0);
			result.setString(NotificationField.DEVICE_TOKEN_ID, deviceTokenBean.getDeviceTokenId());
			if (!userName.equals(user)) {
				getLogger().debug("update user of deviceToken");
				long modifiCount = this.getDeviceTokenModel().updateUserOfDeviceToken(token, user);
				getLogger().debug("modifiCount update DeviceTokenBean: " + modifiCount);
			} else {
				getLogger().debug("userDeviceToken exist in db");
			}
		} else { // insert
			if (this.getContext().getModeTest() == true) {
				getLogger().debug("modeTest - insert new deviceToken to db");
				DeviceTokenMongoBean deviceTokenBean = getDeviceToken(token, "modeTest", user);
				this.getDeviceTokenModel().insert(deviceTokenBean);
				result.setInteger(NotificationField.STATUS, 0);
				result.setString(NotificationField.DEVICE_TOKEN_ID, deviceTokenBean.getDeviceTokenId());
			} else {
				PuObject response = getResponseFromHermes(token);
				if (response.getInteger(NotificationField.STATUS) == 0) {
					getLogger().debug("insert new deviceToken to db");
					DeviceTokenMongoBean deviceTokenBean = getDeviceToken(token,
							response.getString(NotificationField.DEVICE_TOKEN_ID), user);
					this.getDeviceTokenModel().insert(deviceTokenBean);
					result.setInteger(NotificationField.STATUS, 0);
					result.setString(NotificationField.DEVICE_TOKEN_ID, deviceTokenBean.getDeviceTokenId());
				} else {
					getLogger().debug("error when insert new deviceToken to db");
					result.setInteger(NotificationField.STATUS, 1);
					result.setInteger(NotificationField.ERROR, Error.ERROR_REGISTER_PUSH.getCode());
					return result;
				}
			}
		}
		return result;
	}

	private DeviceTokenMongoBean getDeviceToken(String deviceToken, String deviceTokenId, String userName) {
		DeviceTokenMongoBean bean = new DeviceTokenMongoBean();
		bean.setDeviceToken(deviceToken);
		bean.setDeviceType("gcm");
		bean.setDeviceTokenId(deviceTokenId);
		bean.setUser(userName);

		return bean;
	}

	private PuObject getResponseFromHermes(String deviceToken){
		PuObject result = new PuObject();
		
		PuObject data = new PuObject();
		data.setString("command", PushNotificationConfig.REGISTER_TOKEN);
		data.setString("token", deviceToken);
		data.setString("authenticator_id", this.getPushNotificationManager().getAuthenticatiorId());
		data.setString("service_type", "gcm");
		data.setString("application_id", PushNotificationManager.getApplicationId());
//		PuObject response = (PuObject) this.getContext().getApi().call(HandlerCollection.HER_MES, data);
//		if(response !=null ){
//			result = response;
//		}
		return result;
	}
}
