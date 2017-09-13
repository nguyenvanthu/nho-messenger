package com.nho.file.data.avatar;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.file.statics.FileField;

public class AvatarMongoBean extends AbstractMongoBean{
	private static final long serialVersionUID = 1L;
	
	private int type ;
	private String userId ;
	private String name ;
	private String path ;
	
	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(FileField.TYPE, this.type);
		document.put(FileField.USER_ID, this.userId);
		document.put(FileField.NAME, this.name);
		document.put(FileField.PATH, this.path);
		
		return document;
	}
	
	public static AvatarMongoBean fromDocument(Document document){
		AvatarMongoBean bean = new AvatarMongoBean();
		bean.setName(document.getString(FileField.NAME));
		bean.setPath(document.getString(FileField.PATH));
		bean.setType(document.getInteger(FileField.TYPE));
		bean.setUserId(document.getString(FileField.USER_ID));
		
		return bean;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
