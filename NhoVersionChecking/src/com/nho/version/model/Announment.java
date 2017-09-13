package com.nho.version.model;

import com.nhb.common.data.PuObject;
import com.nho.version.statics.Version;

public class Announment {
	private String message ;
	private String label ;
	private String link;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	public PuObject toPuObject(){
		PuObject obj = new PuObject();
		obj.setString(Version.MESSAGE, this.message);
		obj.setString(Version.LABEL, this.label);
		obj.setString(Version.LINK, this.link);
		return obj;
	}
	
	public static Announment fromPuObject(PuObject obj){
		Announment announment = new Announment();
		announment.setLabel(obj.getString(Version.LABEL));
		announment.setLink(obj.getString(Version.LINK));
		announment.setMessage(obj.getString(Version.MESSAGE));
		
		return announment;
	}
}
