package com.nho.server.processors.notification;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.request.notification.TestPushNotification;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
@NhoCommandProcessor(command={MessageType.TEST_PUSH})
public class TestPushNotificationProcessor extends AbstractNhoProcessor<TestPushNotification> {

	@Override
	protected void process(TestPushNotification request) {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		PuObject dataPush = new PuObject();
		dataPush.setInteger(NotificationField.COMMAND, NotifcationCommand.PUSH_BY_USERNAME.getCode());
		dataPush.setString(NotificationField.MESSAGE, "test");
		dataPush.setString(NotificationField.USERNAME, user.getUserName());
		dataPush.setString(NotificationField.TITLE, "test");

		@SuppressWarnings("unused")
		RPCFuture<PuElement> publishGetList = getContext().getNotificationProducer().publish(dataPush);
	}
}
