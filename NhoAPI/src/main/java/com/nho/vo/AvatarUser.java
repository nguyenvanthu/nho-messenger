package com.nho.vo;

import com.nhb.common.data.PuObject;
import com.nho.statics.F;

public class AvatarUser {
	private String name ;
	private String url ;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public static AvatarUser fromPuObject(PuObject puObject){
		AvatarUser avatar = new AvatarUser();
		if(puObject ==null){
			return null;
		}
		if(puObject.variableExists(F.NAME)){
			avatar.setName(puObject.getString(F.NAME));
		}
		if(puObject.variableExists(F.URL)){
			avatar.setUrl(puObject.getString(F.URL));
		}
		
		return avatar;
	}
	
	public PuObject toPuObject(){
		PuObject obj = new PuObject();
		obj.setString(F.NAME, this.name);
		obj.setString(F.URL, this.url);
		
		return obj;
	}
}