package com.nho.notification.data;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.notification.statics.DeviceTokenDbField;

public class DeviceTokenMongoBean extends AbstractMongoBean {
	private static final long serialVersionUID = 1L;

	private String deviceToken;
	private String deviceType;
	private String deviceTokenId;
	private String user;

	public String getDeviceTokenId() {
		return deviceTokenId;
	}

	public void setDeviceTokenId(String deviceTokenId) {
		this.deviceTokenId = deviceTokenId;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public Document toDocument() {
		Document document = new Document();

		document.put(DeviceTokenDbField._ID, this.getObjectId() == null ? new ObjectId() : this.getObjectId());
		document.put(DeviceTokenDbField.DEVICE_TOKEN, this.deviceToken);
		document.put(DeviceTokenDbField.DEVICE_TYPE, this.deviceType);
		document.put(DeviceTokenDbField.DEVICE_TOKEN_ID, this.deviceTokenId);
		document.put(DeviceTokenDbField.USER, this.user);

		return document;
	}

	public static DeviceTokenMongoBean fromDocument(Document document) {
		DeviceTokenMongoBean bean = new DeviceTokenMongoBean();

		bean.setObjectId(document.getObjectId(DeviceTokenDbField._ID));
		bean.setDeviceToken(document.getString(DeviceTokenDbField.DEVICE_TOKEN));
		bean.setDeviceType(document.getString(DeviceTokenDbField.DEVICE_TYPE));
		bean.setDeviceTokenId(document.getString(DeviceTokenDbField.DEVICE_TOKEN_ID));
		bean.setUser(document.getString(DeviceTokenDbField.USER));
		return bean;
	}
}
