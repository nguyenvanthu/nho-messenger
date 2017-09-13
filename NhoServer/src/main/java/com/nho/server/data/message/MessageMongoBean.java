package com.nho.server.data.message;

import org.bson.Document;

import com.nhb.common.data.PuObject;
import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.server.statics.DBF;
import com.nho.statics.F;

public class MessageMongoBean extends AbstractMongoBean {
	private static final long serialVersionUID = 1L;

	private int timestamp;
	private String from;
	private PuObject data;

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public PuObject getData() {
		return data;
	}

	public void setData(PuObject data) {
		this.data = data;
	}

	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(F.TIME_STAMP, this.timestamp);
		document.put(F.FROM, this.from);
		document.put(F.DATA, this.data.toJSON());

		return document;
	}

	public static MessageMongoBean fromDocument(Document document) {
		MessageMongoBean message = new MessageMongoBean();
		message.setFrom(document.getString(F.FROM));
		message.setTimestamp(document.getInteger(F.TIME_STAMP));
		message.setObjectId(document.getObjectId(DBF._ID));
		message.setData(PuObject.fromJSON(document.getString(F.DATA)));

		return message;
	}

}
