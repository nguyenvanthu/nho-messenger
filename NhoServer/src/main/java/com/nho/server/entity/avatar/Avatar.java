package com.nho.server.entity.avatar;

import java.io.Serializable;

import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.statics.AvatarType;

public class Avatar extends BaseEventDispatcher implements Serializable {
	private static final long serialVersionUID = 1L;

	private int type;
	private String name;
	private String url;

	public Avatar() {

	}

	public Avatar(int type, String name, String url) {
		this.type = type;
		this.name = name;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public AvatarType getType() {
		return AvatarType.fromCode(type);
	}

	public void setType(AvatarType type) {
		this.type = type.getCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
