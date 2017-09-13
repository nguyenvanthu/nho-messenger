package com.nho.chat.router.impl;

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

@ChatCommandProcessor(command = { ChannelCommand.LEAVE_CHANNEL })
public class LeaveChannelProcessor extends ChatAbstractProcessor{

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		data.setType(ChatField.LEAVER_USER, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		String leaverUser = data.getString(ChatField.LEAVER_USER);
		
		this.getChannelManager().removeUserInChannel(leaverUser);
		getLogger().debug("user {} leave Channel {}",leaverUser,channelId);
		ChannelMongoBean channel = null;
		channel = this.getChannelMongoModel().findChannelById(channelId);
		if(channel!=null){
			PuArray subcriber = new PuArrayList();
			for(String sub : this.getChannelManager().getUserInChannels(channel)){
				subcriber.addFrom(sub);
			}
			response.setInteger(ChatField.STATUS, 0);
			response.setPuArray(ChatField.SUBCRIBE, subcriber);
			return response;
		}
		return PuObject.fromObject(new MapTuple<>(ChatField.STATUS,1));
	}

}
