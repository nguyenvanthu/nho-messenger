package com.nho.server.data.avatar;

import com.nhb.common.db.beans.UUIDBean;

public class AvatarBean extends UUIDBean{
	private static final long serialVersionUID = 1L;
	
	private String url;
	private int type;
	private String name ;
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	

}
