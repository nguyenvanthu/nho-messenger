package com.nho.chat.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.chat.statics.ChannelDBField;
import com.nho.statics.F;

public class LiveObjectMongoBean extends AbstractMongoBean {
	private static final long serialVersionUID = 1L;

	private String liveObjId;
	private String channelId;
	private String owner = "";

	private float x;
	private float y;
	private List<StrokeMongoBean> strokes;

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public List<StrokeMongoBean> getStrokes() {
		return strokes;
	}

	public void setStrokes(List<StrokeMongoBean> strokes) {
		this.strokes = strokes;
	}

	public String getLiveObjId() {
		return liveObjId;
	}

	public void setLiveObjId(String liveObjId) {
		this.liveObjId = liveObjId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(F.OBJ_ID, this.liveObjId);
		document.put(ChannelDBField.CHANNEL_ID, this.channelId);
		document.put(ChannelDBField.OWNER, this.owner);
		document.put(ChannelDBField.X, String.valueOf(this.x));
		document.put(ChannelDBField.Y, String.valueOf(this.y));
		Document strokeDocuments = new Document();
		int i = 1;
		for (StrokeMongoBean bean : this.strokes) {
			strokeDocuments.put(i + "", bean.toDocument());
			i += 1;
		}
		document.put(ChannelDBField.STROKES, strokeDocuments);
		return document;
	}

	public static LiveObjectMongoBean fromDocument(Document document) {
		LiveObjectMongoBean bean = new LiveObjectMongoBean();
		bean.setChannelId(document.getString(F.CHANNEL_ID));
		bean.setOwner(document.getString(ChannelDBField.OWNER));
		bean.setLiveObjId(document.getString(ChannelDBField.OBJ_ID));
		bean.setX(Float.parseFloat(document.getString(ChannelDBField.X)));
		bean.setY(Float.parseFloat(document.getString(ChannelDBField.Y)));
		List<StrokeMongoBean> strokeBeans = new ArrayList<>();
		Document strokes = (Document) document.get(ChannelDBField.STROKES);
		int index = 1;
		while (strokes.containsKey(index + "")) {
			strokeBeans.add(StrokeMongoBean.fromDocument((Document) strokes.get(index + "")));
			index += 1;
		}
		bean.setStrokes(strokeBeans);

		return bean;
	}

	public String toString() {
		Gson gSon = new Gson();
		getLogger().debug("json value: " + gSon.toJson(this));
		return gSon.toJson(this);
	}
}
