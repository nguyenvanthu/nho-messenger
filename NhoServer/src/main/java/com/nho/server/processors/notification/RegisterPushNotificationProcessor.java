package com.nho.server.processors.notification;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.notification.RegisterPushNotificationRequest;
import com.nho.message.response.notification.RegisterPushNotificationResponse;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;
@NhoCommandProcessor(command={MessageType.REGISTER_PUSH_NOTIFICATION})
public class RegisterPushNotificationProcessor extends AbstractNhoProcessor<RegisterPushNotificationRequest> {

	@Override
	protected void process(RegisterPushNotificationRequest request) {
		RegisterPushNotificationResponse response = new RegisterPushNotificationResponse();
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());

		if (user == null || request.getToken() == null) {
			response.setSuccess(false);
			response.setError(Error.USER_NOT_LOGGED_IN);
			this.send(response, request.getSessionId());
			return;
		}
		
		if(this.getUserManager().isUserOnline(user.getUserName())){
			this.getUserManager().addNewDeviceInApp(request.getSessionId(), request.getToken());
		}

		PuObject data = new PuObject();
		data.setInteger(NotificationField.COMMAND, NotifcationCommand.REGISTER_TOKEN.getCode());
		data.setString(NotificationField.USERNAME, user.getUserName());
		data.setString(NotificationField.DEVICE_TOKEN, request.getToken());

		RPCFuture<PuElement> pushlish = this.getContext().getNotificationProducer().publish(data);
		try {
			PuElement puElement = pushlish.get();
			PuObject result = (PuObject) puElement;
			int status = result.getInteger(FriendField.STATUS);
			if(status ==0){
				result.setType(NotificationField.DEVICE_TOKEN_ID, PuDataType.STRING);
				String deviceTokenId = result.getString(NotificationField.DEVICE_TOKEN_ID);
				response.setSuccess(true);
				response.setDeviceTokenId(deviceTokenId);
				this.send(response, user.getSessions());
			}else {
				response.setSuccess(false);
				response.setError(Error.fromCode(result.getInteger(NotificationField.ERROR)));
				this.send(response, request.getSessionId());
				return;
			}
		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
			response.setSuccess(false);
			response.setError(Error.ERROR_RABBIT_MQ);
			this.send(response, request.getSessionId());
			return;
		}

	}

}
