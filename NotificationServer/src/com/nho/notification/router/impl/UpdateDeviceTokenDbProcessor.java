package com.nho.notification.router.impl;

import java.util.List;

import org.bson.Document;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.notification.annotation.NotificationCommandProcessor;
import com.nho.notification.data.DeviceTokenMongoBean;
import com.nho.notification.exception.NotificationException;
import com.nho.notification.router.NotificationAbstractProcessor;
import com.nho.notification.statics.DeviceTokenDbField;
import com.nho.notification.statics.NotifcationCommand;

@NotificationCommandProcessor(command = { NotifcationCommand.UPDATE_DEVICE_TOKEN_DB })
public class UpdateDeviceTokenDbProcessor extends NotificationAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws NotificationException {
		List<Document> deviceTokenDocuments = this.getDeviceTokenModel().getAllDeviceTokenDocument();
		for(Document doc : deviceTokenDocuments){
			this.getDeviceTokenModel().deleteDeviceTokenDocument(doc);
			DeviceTokenMongoBean bean = new DeviceTokenMongoBean();
			bean.setObjectId(doc.getObjectId(DeviceTokenDbField._ID));
			bean.setDeviceToken(doc.getString(DeviceTokenDbField.DEVICE_TOKEN));
			bean.setDeviceType(doc.getString(DeviceTokenDbField.DEVICE_TYPE));
			bean.setDeviceTokenId(doc.getString(DeviceTokenDbField.DEVICE_TOKEN_ID));
			bean.setUser(doc.getString("user.userName"));
			
			this.getDeviceTokenModel().insert(bean);
		}
		return null;
	}

}
