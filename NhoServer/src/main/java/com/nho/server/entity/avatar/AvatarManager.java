package com.nho.server.entity.avatar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.statics.AvatarType;

public class AvatarManager extends BaseEventDispatcher {

	private static Map<String, Avatar> avatarMapping = new ConcurrentHashMap<>();

	public Map<String, Avatar> getAvatars() {
		return avatarMapping;
	}

	public void registerAvatar(AvatarType type, String name,String url) {

		Avatar avatar = new Avatar();
		
		avatar.setType(type);
		avatar.setName(name);
		avatar.setUrl(url);
		avatarMapping.put(avatar.getName(), avatar);
		getLogger().debug("register avatar "+avatar.getName());
	}
	
	public Avatar getAvatarByName(String name){
		if(avatarMapping.containsKey(name)){
			return avatarMapping.get(name);
		}
		return null;
	}
}
