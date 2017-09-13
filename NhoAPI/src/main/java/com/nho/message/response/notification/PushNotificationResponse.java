package com.nho.message.response.notification;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.PushNotificationType;

public class PushNotificationResponse extends NhoMessage{
	{
		this.setType(MessageType.PUSH_NOTIFICATION_RESPONSE);
	}
	private PushNotificationType pushType ;
	private String title ;
	private String message;
	
	public PushNotificationType getPushType() {
		return pushType;
	}
	public void setPushType(PushNotificationType pushType) {
		this.pushType = pushType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.pushType.getCode());
		puArray.addFrom(this.title);
		puArray.addFrom(this.message);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.pushType = PushNotificationType.fromCode(puArray.remove(0).getInteger());
		this.title = puArray.remove(0).getString();
		this.message = puArray.remove(0).getString();
	}
}
