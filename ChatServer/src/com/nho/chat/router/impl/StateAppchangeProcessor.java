package com.nho.chat.router.impl;

import java.util.List;

import com.nhb.common.data.MapTuple;
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

@ChatCommandProcessor(command = { ChannelCommand.STATE_APP_CHANGE})
public class StateAppchangeProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.SENDER_NAME, PuDataType.STRING);
		String senderUser = data.getString(ChatField.SENDER_NAME);

		List<String> currentChannelIds = this.getChannelManager().getChannelIdByUserName(senderUser);
		for (String currentChannelId : currentChannelIds) {
			ChannelMongoBean channel = this.getChannelMongoModel().findChannelById(currentChannelId);
			if (channel != null) {
				response.setInteger(ChatField.STATUS, 0);
				PuArray subcriber = new PuArrayList();
				for (String sub : this.getChannelManager().getUserInChannels(channel)) {
					subcriber.addFrom(sub);
				}
				response.setPuArray(ChatField.SUBCRIBE, subcriber);
				getLogger().debug(
						"number subcriber in list " + this.getChannelManager().getUserInChannels(channel).size());
				return response;
			}
		}
		return PuObject.fromObject(new MapTuple<>(ChatField.STATUS, 1));
	}

}
