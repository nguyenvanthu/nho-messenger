package com.nho.notification.entity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.nhb.common.data.PuObject;
import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.notification.NotificationHandler;
import com.nho.notification.data.DeviceTokenModel;
import com.nho.notification.data.DeviceTokenMongoBean;
import com.nho.notification.push.Content;
import com.nho.notification.push.Push2GCM;
import com.nho.notification.statics.PushNotificationConfig;
import com.nho.statics.F;

public class PushNotificationManager extends BaseEventDispatcher {
	private NotificationHandler context;
	private Map<List<String>, String> userTokensMapping = new HashMap<>();
	public static String Application_Id = null;
	private String authenticatiorId;
	private String API_Key = "AIzaSyAXDgB5MLtvJ1fxU8gQ4-UJtoOcj6zk8Yo";
	private static final String SERVICE_TYPE = "gcm";
	private static final boolean SAND_BOX = false;
	private DeviceTokenModel deviceTokenModel;
	
	private DeviceTokenModel getDeviceTokenModel(){
		if(this.deviceTokenModel == null ){
			this.deviceTokenModel = getContext().getModelFactory().newModel(DeviceTokenModel.class);
		}
		return this.deviceTokenModel;
	}

	public String getAuthenticatiorId() {
		return authenticatiorId;
	}

	public void setAuthenticatiorId(String authenticatiorId) {
		this.authenticatiorId = authenticatiorId;
	}

	public static String getApplicationId() {
		if (Application_Id == null) {
			Application_Id = UUID.randomUUID().toString();
		}
		return Application_Id;
	}

	public PushNotificationManager(NotificationHandler context) {
		this.context = context;
	}

	public NotificationHandler getContext() {
		return this.context;
	}

	public List<String> getTokenOfUser(String userName) {
		for (Entry<List<String>, String> entry : this.userTokensMapping.entrySet()) {
			if (userName.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void addNewDeviceToken(String deviceToken, String userName) {
		if (this.userTokensMapping.containsValue(userName)) {
			List<String> deviceTokens = getTokenOfUser(userName);
			if (deviceTokens != null & !deviceTokens.contains(deviceToken)) {
				deviceTokens.add(deviceToken);
				this.userTokensMapping.put(deviceTokens, userName);
			}
		} else {
			List<String> deviceTokens = new ArrayList<>();
			deviceTokens.add(deviceToken);
			this.userTokensMapping.put(deviceTokens, userName);
		}
	}

	public Map<List<String>, String> getUserTokensMapping() {
		return this.userTokensMapping;
	}

	public void pushNotificationByHermes(String message, String title,String deviceTokenId) throws IOException {
//		PuObject puObject = getBodyResponse(getUrlPushNotification(message, title,deviceTokenId));
//		if (puObject != null) {
//			int status = puObject.getInteger(F.STATUS);
//			if (status == 0) {
//				System.out.println("push notification successful");
//			}
//		}
		PuObject data = new PuObject();
		data.setString(F.COMMAND, PushNotificationConfig.PUSH_COMMAND);
		data.setString(F.APPLICATION_ID, getApplicationId());
		data.setString("token", deviceTokenId);
		data.setString("service_type", "gcm");
		data.setString(F.MESSAGE, message);
		data.setString("title", title);
		
//		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.HER_MES, data);
//		if(result!=null){
//			int status = result.getInteger(F.STATUS);
//			if(status == 0){
//				getLogger().debug("push notification by hermes sucessful");
//			}
//		}
	}
	
//	private String getUrlPushNotification(String message, String title,String deviceTokenId) {
//		String url = this.getContext().getHermesHost() + PushNotificationConfig.PUSH;
//		url = url + PushNotificationConfig.PUSH_COMMAND + "&" + F.APPLICATION_ID + "=" + getApplicationId() + "&"
//				+ F.SANDBOX + "=" + SAND_BOX + "&" + F.TOKEN + "=" + deviceTokenId + "&" + F.SERVICE_TYPE + "="
//				+ SERVICE_TYPE + "&" + F.MESSAGE + "=" + message + "&" + F.TITLE + "=" + title;
//		getLogger().debug("url push notification " + url);
//		return url;
//	}

	public void addAuthenticationAndroidPlatform() {
		if (this.getAuthenticatiorId() == null) {
//			PuObject puObject = getBodyResponse(getUrlAddAuthenticationAndroidPlatform());
//			if (puObject != null) {
//				int status = puObject.getInteger(F.STATUS);
//				if (status == 0) {
//					this.authenticatiorId = puObject.getString(F.AUTHENTICATOR_ID);
//					getLogger().debug("authentiorId is " + this.authenticatiorId);
//				}
//			}
			
			PuObject data = new PuObject();
			data.setString(F.COMMAND, PushNotificationConfig.ADD_AUTHENTICATION);
			data.setString(F.APPLICATION_ID, getApplicationId());
			data.setString("service_type", SERVICE_TYPE);
			data.setBoolean("sandbox", SAND_BOX);
			data.setRaw("authenticator", API_Key.getBytes());
			
//			PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.HER_MES, data);
//			int status = result.getInteger(F.STATUS);
//			if(status == 0){
//				this.authenticatiorId = result.getString(F.AUTHENTICATOR_ID);
//				getLogger().debug("authentiorId is " + this.authenticatiorId);
//			}
		}
	}


	public void pushToGCM(String message, String title, String regId) {
		getLogger().debug("push notification by simple Push2GCM");
		Content content = Content.createContent(regId, message, title);
		try{
			Push2GCM.post(API_Key, content);
		}catch(Exception exception){
			getLogger().debug("error when push directly notication "+exception);
		}
	}

	public PuObject getBodyResponse(String url) {
		try {
			String body = "";
			URL hermesHost = new URL(url);
			URLConnection connection = hermesHost.openConnection();
			InputStream stream;
			stream = connection.getInputStream();
			String encoding = connection.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			body = IOUtils.toString(stream, encoding);
			PuObject puObject = PuObject.fromJSON(body);
			return puObject;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public void pushNotification(String message,String title ,String userName){
		List<DeviceTokenMongoBean> deviceTokenBeans = this.getDeviceTokenModel().findByUserName(userName);
		if (this.getContext().getModeTest()) {
			List<String> deviceTokens = new ArrayList<>();
			for (DeviceTokenMongoBean bean : deviceTokenBeans) {
				deviceTokens.add(bean.getDeviceToken());
			}
			for (String deviceToken : deviceTokens) {
				getLogger().debug("send push notifcation");
				this.pushToGCM(message, title, deviceToken);
			}
		}else {
			try {
				List<String> deviceTokenIds = new ArrayList<>();
				for (DeviceTokenMongoBean bean : deviceTokenBeans) {
					deviceTokenIds.add(bean.getDeviceTokenId());
				}
				for (String deviceTokenId : deviceTokenIds) {
					getLogger().debug("send push notifcation by hermes");
					this.pushNotificationByHermes(message, title, deviceTokenId);
				}
			} catch (IOException e) {
				getLogger().debug("error when push notification using hermes");
				e.printStackTrace();
			}
		}
	}
}
