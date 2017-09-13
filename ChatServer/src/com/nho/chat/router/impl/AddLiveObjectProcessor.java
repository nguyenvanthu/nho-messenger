package com.nho.chat.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.data.LiveObjectMongoBean;
import com.nho.chat.data.StrokeMongoBean;
import com.nho.chat.entity.BasicLiveObjectInfo;
import com.nho.chat.entity.Position;
import com.nho.chat.entity.Stroke;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.ADD_LIVE_OBJECT })
public class AddLiveObjectProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		response.setInteger(ChatField.STATUS, 1);
		PuObject data = (PuObject) request;
		data.setType(ChatField.OBJ_ID, PuDataType.STRING);
		data.setType(ChatField.SENDER_NAME, PuDataType.STRING);
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		// String userName = data.getString(ChatField.SENDER_NAME);
		String owner = data.getString(ChatField.SENDER_NAME);
		String objId = data.getString(ChatField.OBJ_ID);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		int startId = data.getInteger(ChatField.START_ID);
		int endId = data.getInteger(ChatField.END_ID);
		float x = data.getFloat(ChatField.X);
		float y = data.getFloat(ChatField.Y);
		BasicLiveObjectInfo stroke = new BasicLiveObjectInfo(startId, endId, new Position(x, y));
		// this.getChatManager().addNewBlockedObj(objId, userName);
		this.getChatManager().addNewObjWithListOfIds(objId, stroke);
		this.getChatManager().addNewObjInChannel(objId, channelId);
		// store live object to database
		String dataLiveObject = storeLiveObject(owner, channelId, objId, x, y);
		if (dataLiveObject != null) {
			response.setInteger(ChatField.STATUS, 0);
			response.setString(ChatField.LIVE_OBJ_DATA, dataLiveObject);
		}
		return response;
	}

	private String storeLiveObject(String owner, String channelId, String objectId, float x, float y) {
		String data = null;
		getLogger().debug("store live object to mongodb ");
		List<Stroke> strokes = this.getChatManager().getStrokesOfLiveObject(objectId);
		if (strokes == null) {
			return null;
		}
		List<StrokeMongoBean> strokeBeans = new ArrayList<>();
		getLogger().debug("number strokes in live object is " + strokes.size());
		for (Stroke stroke : strokes) {
			strokeBeans.add(stroke.toStrokeMongoBean());
		}
		LiveObjectMongoBean liveObjectBean = new LiveObjectMongoBean();
		liveObjectBean.setOwner(owner);
		liveObjectBean.setChannelId(channelId);
		liveObjectBean.setLiveObjId(objectId);
		liveObjectBean.setX(x);
		liveObjectBean.setY(y);
		liveObjectBean.setStrokes(strokeBeans);
		if (this.getLiveObjectModel().insert(liveObjectBean)) {
			// insert success to database
			data = liveObjectBean.toString();
		}
		return data;
	}
}
