package com.nho.server.helper;

import java.util.Collection;

import com.nhb.common.data.PuObject;
import com.nho.message.NhoMessage;
import com.nho.message.response.notification.PushNotificationResponse;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.server.NhoServer;
import com.nho.server.entity.user.User;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.PushNotificationType;

public class NotificationHelper extends AbstractHelper{
	public NotificationHelper(NhoServer context) {
		super.setContext(context);
	}
	
	public void pushInApp(String message, String title,User receiver,PushNotificationType type) {
		getLogger().debug("send api push in app");
		PushNotificationResponse pushResponse = new PushNotificationResponse();
		pushResponse.setMessage(message);
		pushResponse.setTitle(title);
		pushResponse.setPushType(type);
		this.send(pushResponse, receiver.getSessions());
	}
	
	public void pushByGCM(String message, String receiverName, String deviceToken,String title) {
		PuObject dataPush = new PuObject();
		dataPush.setInteger(NotificationField.COMMAND, NotifcationCommand.PUSH_BY_USERNAME.getCode());
		dataPush.setString(NotificationField.MESSAGE, message);
		dataPush.setString(NotificationField.USERNAME, receiverName);
		dataPush.setString(NotificationField.TITLE, title);
		dataPush.setString(NotificationField.DEVICE_TOKEN, deviceToken);
		this.getContext().getApi().call(HandlerCollection.NOTIFICATION_SERVER, dataPush);
	}
	protected void send(NhoMessage message, String... sessionIds) {
		this.getContext().send(message, sessionIds);
	}

	public void send(NhoMessage message, Collection<String> sessionIds) {
		this.getContext().send(message, sessionIds);
	}
}
