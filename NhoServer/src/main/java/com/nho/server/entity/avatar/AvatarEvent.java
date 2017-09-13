package com.nho.server.entity.avatar;

import com.nhb.eventdriven.impl.AbstractEvent;

public class AvatarEvent extends AbstractEvent {
	public static final String AVATAR_ADDED ="avatarAdded";
	public static final String AVATAR_REMOVED = "avatarRemoved";
	
	public static AvatarEvent createAvatarAddedEvent(String url){
		return new AvatarEvent(AVATAR_ADDED,url);
	}
	
	public static AvatarEvent createAvatarRemovedEvent(byte[] avatarId){
		return new AvatarEvent(AVATAR_REMOVED, avatarId);
	}
	
	private String name ;
	private byte[] avatarId ;
	
	public AvatarEvent(String type , String name){
		this.setType(type);
		this.setName(name);
	}
	
	public AvatarEvent(String type,byte[] avatarId){
		this.setType(type);
		this.setAvatarId(avatarId);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public byte[] getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(byte[] avatarId) {
		this.avatarId = avatarId;
	}
}