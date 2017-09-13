package com.nho.tracking.model;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.tracking.EF;
import com.nho.tracking.statics.ExceptionType;

public class ExceptionBean extends AbstractMongoBean{
	private static final long serialVersionUID = 1L;
	
	private ExceptionType type ;
	private String stackTrace ;
	private String title ;
	
	public ExceptionType getType() {
		return type;
	}

	public void setType(ExceptionType type) {
		this.type = type;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(EF.TYPE, this.type.getCode());
		document.put(EF.TITLE, this.title);
		document.put(EF.STACKTRACE, this.stackTrace);
		
		return document;
	}

	public static ExceptionBean fromDocument(Document document){
		ExceptionBean bean = new ExceptionBean();
		bean.setObjectId(document.getObjectId(EF._ID));
		bean.setStackTrace(document.getString(EF.STACKTRACE));
		bean.setTitle(EF.TITLE);
		bean.setType(ExceptionType.fromCode(document.getInteger(EF.TYPE)));
		
		return bean;
	}
}
