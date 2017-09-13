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
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.GET_DATA_MSG_BOT })
public class GetDataMsgChatBotProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.USER_NAME, PuDataType.STRING);
		String userName = data.getString(ChatField.USER_NAME);
		List<String> dataMsgs = this.getChatManager().getMessageChatOfUser(userName);
		if (dataMsgs.size() > 0) {
			PuObject result = new PuObject();
			result.setInteger(ChatField.STATUS, 0);
			PuArray array = new PuArrayList();
			for (String msg : dataMsgs) {
				array.addFrom(msg);
			}
			result.setPuArray(ChatField.DATA_MESSAGE, array);
			return result;
		}
		// remove data message chat 
		this.getChatManager().removeUserChatWithBot(userName);
		return PuObject.fromObject(new MapTuple<>(ChatField.STATUS, 1));
	}

}
