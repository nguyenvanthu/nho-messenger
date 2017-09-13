package com.nho.server.task.impl;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.router.impl.UpdateFriendDbProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.notification.statics.NotificationField;
import com.nho.server.NhoServer;
import com.nho.server.task.AbstractTask;

public class UpdateDbTask extends AbstractTask {
	public UpdateDbTask(NhoServer context) {
		super.setContext(context);
	}

	@Override
	public void run() {
		this.updateDeviceTokenCollection();
		this.updateFriendCollection();
	}

	/**
	 * update friend data base 
	 * send command {@link FriendCommand#UPDATE_FRIEND_DB} to {@link UpdateFriendDbProcessor} 
	 */
	private void updateFriendCollection() {
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, FriendCommand.UPDATE_FRIEND_DB.getCode());
		@SuppressWarnings("unused")
		RPCFuture<PuElement> publish = this.getContext().getFriendProducer().publish(data);
	}

	private void updateDeviceTokenCollection() {
		PuObject data = new PuObject();
		data.setInteger(NotificationField.COMMAND, NotifcationCommand.UPDATE_DEVICE_TOKEN_DB.getCode());
		@SuppressWarnings("unused")
		RPCFuture<PuElement> publish = this.getContext().getNotificationProducer().publish(data);
	}
}
