package com.nho.chat.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.data.PuValue;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.DELETE_OBJ })
public class DeleteLiveObjectProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		List<String> objIds = new ArrayList<String>();
		PuArray array = data.getPuArray(ChatField.OBJ_IDS);
		for(PuValue value : array){
			objIds.add(value.getString());
		}
		for(String objId : objIds){
			this.getChatManager().removeBlockedObj(objId);
			this.getChatManager().removeObjWithListOfIds(objId);
			this.getChatManager().removeObjIdInChannel(objId);
			this.getLiveObjectModel().deleteLiveObject(objId);
		}
		return PuObject.fromObject(new MapTuple<>(ChatField.STATUS, 0));
	}

}
