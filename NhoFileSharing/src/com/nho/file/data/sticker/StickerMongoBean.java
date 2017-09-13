package com.nho.file.data.sticker;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.file.statics.FileField;

public class StickerMongoBean extends AbstractMongoBean {
	private static final long serialVersionUID = 1L;

	private int type;
	private String name;
	private String group;
	private String path;

	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(FileField.TYPE, this.type);
		document.put(FileField.NAME, this.name);
		document.put(FileField.GROUP, this.group);
		document.put(FileField.PATH, this.path);

		return document;
	}

	public static StickerMongoBean fromDocument(Document document) {
		StickerMongoBean bean = new StickerMongoBean();

		bean.setGroup(document.getString(FileField.GROUP));
		bean.setName(document.getString(FileField.NAME));
		bean.setPath(document.getString(FileField.PATH));
		bean.setType(document.getInteger(FileField.TYPE));

		return bean;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
