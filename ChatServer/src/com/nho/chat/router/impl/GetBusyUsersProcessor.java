package com.nho.chat.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
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

@ChatCommandProcessor(command = { ChannelCommand.GET_LIST_USER_BUSY })
public class GetBusyUsersProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		data.setType(ChatField.SENDER_NAME, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		String senderUser = data.getString(ChatField.SENDER_NAME);
		List<String> userBuzys = getListUserBuzy(channelId, senderUser);
		PuArray array = new PuArrayList();
		for (String user : userBuzys) {
			array.addFrom(user);
		}
		response.setInteger(ChatField.STATUS, 0);
		response.setPuArray(ChatField.LIST_USER, array);
		return null;
	}

	private List<String> getListUserBuzy(String channelId, String from) {
		List<String> userBuzys = new ArrayList<>();
		ChannelMongoBean channel = this.getChannelMongoModel().findChannelById(channelId);
		if (channel != null) {
			for (String user : this.getChannelManager().getUserInChannels(channel)) {
				if (!user.equals(from)) {
					if (this.getChannelManager().isUserBusy(channelId, user)) {
						userBuzys.add(user);
					}
				}
			}
		}
		return userBuzys;
	}
}
