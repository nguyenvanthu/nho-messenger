package com.nho.file.data.game;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.file.statics.FileField;

public class GameMongoBean extends AbstractMongoBean{
	private static final long serialVersionUID = 1L;
	
	private int type ;
	private String name ;
	private String pathData ;
	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(FileField.TYPE, this.type);
		document.put(FileField.NAME, this.name);
		document.put(FileField.PATH, this.pathData);
		
		return document;
	}
	
	public static GameMongoBean fromDocument(Document document ){
		GameMongoBean bean = new GameMongoBean();
		bean.setName(document.getString(FileField.NAME));
		bean.setPathData(document.getString(FileField.PATH));
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
	public String getPathData() {
		return pathData;
	}
	public void setPathData(String pathData) {
		this.pathData = pathData;
	}

}
