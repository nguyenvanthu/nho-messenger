package com.nho.server.processors.login;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.request.login.LogoutRequest;
import com.nho.message.response.login.LogoutResponse;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.IdHandlerException;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.LOGOUT })
public class LogoutProcessor extends AbstractNhoProcessor<LogoutRequest> {

	@Override
	protected void process(final LogoutRequest request) {
		IdHandlerException handlerException = new IdHandlerException();
		Thread.setDefaultUncaughtExceptionHandler(handlerException);
		getLogger().debug("receive logout request ");
		LogoutResponse response = new LogoutResponse();
		User user = getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null) {
			getLogger().debug("error user not login in logout process");
			response.setSuccess(false);
			response.setError(Error.USER_NOT_LOGGED_IN);
			this.send(response, request.getSessionId());
			return;
		}
		response.setSuccess(true);
		getLogger().debug("send logout response to user");
		this.send(response, request.getSessionId());
		this.getUserManager().whenUserLogout(user.getUserName(), request.getSessionId());
		sendLogoutLog(request, user);
		// delete token of user in database
		String token = request.getDeviceToken();

		PuObject data = new PuObject();
		data.setInteger(NotificationField.COMMAND, NotifcationCommand.REMOVE_TOKEN.getCode());
		data.setString(NotificationField.DEVICE_TOKEN, token);

		@SuppressWarnings("unused")
		RPCFuture<PuElement> publishGetList = getContext().getNotificationProducer().publish(data);
	}

	private void sendLogoutLog(LogoutRequest request, User user) {
		String content = "user " + user.getUserName() + " logout";
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.LOGOUT, user.getUserName(), request.getSessionId());
	}
}
