package com.nho.notification.router;

import com.nhb.common.BaseLoggable;
import com.nho.notification.NotificationHandler;
import com.nho.notification.data.DeviceTokenModel;
import com.nho.notification.entity.PushNotificationManager;

public abstract class NotificationAbstractProcessor extends BaseLoggable implements NotificationProcessor {

	private NotificationHandler context ;
	private DeviceTokenModel deviceTokenModel;
	
	public NotificationHandler getContext() {
		return context;
	}

	public void setContext(NotificationHandler context) {
		this.context = context;
	}
	
	protected DeviceTokenModel getDeviceTokenModel(){
		if(this.deviceTokenModel == null ){
			this.deviceTokenModel = getContext().getModelFactory().newModel(DeviceTokenModel.class);
		}
		return this.deviceTokenModel;
	}
	
	protected PushNotificationManager getPushNotificationManager(){
		return this.getContext().getPushNotificationManager();
	}
	
}
