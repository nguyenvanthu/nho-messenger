package com.nho.chat.data;

import org.bson.Document;

import com.google.gson.Gson;
import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.chat.statics.ChannelDBField;

public class StrokeMongoBean extends AbstractMongoBean {

	private static final long serialVersionUID = 1L;
	private int index;
	private int action;
	private float x;
	private float y;
	private String uuid;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

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


	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(ChannelDBField.ID, this.uuid);
		document.put(ChannelDBField.INDEX, this.index);
		document.put(ChannelDBField.ACTION, this.action);
		document.put(ChannelDBField.X, String.valueOf(this.x));
		document.put(ChannelDBField.Y, String.valueOf(this.y));
		return document;
	}

	public static StrokeMongoBean fromDocument(Document document) {
		StrokeMongoBean bean = new StrokeMongoBean();
		bean.setUuid(document.getString(ChannelDBField.ID));
		bean.setAction(document.getInteger(ChannelDBField.ACTION));
		bean.setIndex(document.getInteger(ChannelDBField.INDEX));
		bean.setX(Float.parseFloat(document.getString(ChannelDBField.X)));
		bean.setY(Float.parseFloat(document.getString(ChannelDBField.Y)));
		return bean;
	}
	
	public String toString(){
		Gson gSon = new Gson();
		String jsonData = gSon.toJson(this);
		getLogger().debug("value of stroke: "+jsonData);
		return jsonData;
	}
}
