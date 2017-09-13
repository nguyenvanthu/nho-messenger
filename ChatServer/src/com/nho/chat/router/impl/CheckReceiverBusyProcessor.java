package com.nho.chat.router.impl;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.data.ChannelMongoBean;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.CHECK_RECEIVER_BUSY })
public class CheckReceiverBusyProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		data.setType(ChatField.SENDER_NAME, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		String senderUser = data.getString(ChatField.SENDER_NAME);
		boolean isRecieverBuzy = isReceiverBusy(channelId, senderUser);
		response.setBoolean(ChatField.IS_BUSY, isRecieverBuzy);
		return response;
	}

	private boolean isReceiverBusy(String channelId, String sender) {
		boolean isBusy = false;
		ChannelMongoBean channel = getChannel(channelId);
		String receiver = "";
		if (channel != null) {
			for (String sub : this.getChannelManager().getUserInChannels(channel)) {
				if (!sub.equals(sender)) {
					receiver = sub;
				}
			}
			if (receiver != null) {
				isBusy = this.getChannelManager().isUserBusy(channelId, receiver);
			}
		}
		return isBusy;
	}

	private ChannelMongoBean getChannel(String channelId) {
		ChannelMongoBean channel = null;
		channel = this.getChannelMongoModel().findChannelById(channelId);
		return channel;
	}

}
