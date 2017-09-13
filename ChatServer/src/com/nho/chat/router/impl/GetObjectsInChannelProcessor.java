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
import com.nho.chat.data.LiveObjectMongoBean;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.GET_LIST_OBJ_CHANNEL })
public class GetObjectsInChannelProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		getLogger().debug("receive command get list live object from Nho Server ");
		PuObject data = (PuObject) request;
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		getLogger().debug("query live object of channel " + channelId);
		List<LiveObjectMongoBean> liveObjs = this.getLiveObjectModel().findByChannelId(channelId);
		if (liveObjs.size() == 0) {
			getLogger().debug("found 0 result from mongodb");
			return PuObject.fromObject(new MapTuple<>(ChatField.STATUS, 1));
		}
		getLogger().debug("number live object bean " + liveObjs.size());
		PuArray liveObjectArray = new PuArrayList();
		for(LiveObjectMongoBean liveObjBean : liveObjs){
			liveObjectArray.addFrom(liveObjBean.toString());
		}
		PuObject result = new PuObject();
		result.setPuArray(ChatField.LIVE_OBJS, liveObjectArray);
		result.setInteger(ChatField.STATUS, 0);

		return result;
	}

}
