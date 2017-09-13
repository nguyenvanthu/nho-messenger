package com.nho.chat.router.impl;

import java.util.List;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
/**
 * check user B is busy when chat with user A
 */
@ChatCommandProcessor(command = { ChannelCommand.CHECK_USER_BUSY })
public class CheckUserBusyProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.INVITED_USER, PuDataType.STRING);
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		String invitedUser = data.getString(ChatField.INVITED_USER);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		boolean isBuzy = isReceiverBuzy(invitedUser, channelId);
		response.setBoolean(ChatField.IS_BUSY, isBuzy);
		getLogger().debug("is user buzy ? " + isBuzy);
		return response;
	}

	private boolean isReceiverBuzy(String invitedUserName, String channelId) {
		boolean isBuzy = false;
		List<String> currentChannelIds = this.getChannelManager().getChannelIdByUserName(invitedUserName);
		if(currentChannelIds.size()>0){
			for (String currentChannelId : currentChannelIds) {
				if (!currentChannelId.equals(channelId)) {
					isBuzy = true;
				}
			}
		}
		return isBuzy;
	}
}
