package com.nho.chat.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.data.LiveObjectMongoBean;
import com.nho.chat.data.StrokeMongoBean;
import com.nho.chat.entity.Stroke;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.ADD_STROKE })
public class AddStrokeProcessor extends ChatAbstractProcessor {
	private static final String GAME_DATA = "game";
	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject data = (PuObject) request;
		data.setType(ChatField.OBJ_ID, PuDataType.STRING);
		data.setType(ChatField.DATA, PuDataType.STRING);
		data.setType(ChatField.DATA_TYPE, PuDataType.STRING);
		data.setType(ChatField.SENDER_NAME, PuDataType.STRING);
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		String owner = data.getString(ChatField.SENDER_NAME);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		String dataType = data.getString(ChatField.DATA_TYPE);
		boolean isEnd = data.getBoolean(ChatField.IS_END);
		String objectId = data.getString(ChatField.OBJ_ID);
		String dataStroke = data.getString(ChatField.DATA);
		getLogger().debug("isEnd: {} && data type : {}",isEnd,dataType);
		storeStrokeOfObject(objectId, dataStroke);
		if(isEnd && dataType.equals(GAME_DATA)){
			// draw game finish, send object data to client 
			PuObject response = new PuObject();
			response.setInteger(ChatField.STATUS, 1);
			LiveObjectMongoBean liveObject = storeLiveObject(owner, channelId, objectId, 0, 0);
			if (liveObject != null) {
				getLogger().debug("store live object success");
				response.setInteger(ChatField.STATUS, 2);
				response.setString(ChatField.LIVE_OBJ_DATA, liveObject.toString());
				response.setInteger(ChatField.START_ID, 1);
				response.setInteger(ChatField.END_ID, liveObject.getStrokes().size());
			}
			return response;
		}
		return PuObject.fromObject(new MapTuple<>(ChatField.STATUS, 0));
	}

	private void storeStrokeOfObject(String objectId, String dataLiveObject) {
		getLogger().debug("stroke data: " + dataLiveObject);
		for (Stroke stroke : getStrokes(dataLiveObject)) {
			this.getChatManager().addStokeOfObjectId(stroke, objectId);
		}
	}

	public List<Stroke> getStrokes(String data) {

		com.google.common.reflect.TypeToken<List<Stroke>> token = new com.google.common.reflect.TypeToken<List<Stroke>>() {
			private static final long serialVersionUID = 1L;
		};
		Gson gSon = new Gson();
		List<Stroke> points = gSon.fromJson(data, token.getType());
		getLogger().debug("receveive {} stroke", points.size());
		return points;
	}
	private LiveObjectMongoBean storeLiveObject(String owner, String channelId, String objectId, float x, float y) {
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
			return liveObjectBean;
		}
		return null;
	}
}
