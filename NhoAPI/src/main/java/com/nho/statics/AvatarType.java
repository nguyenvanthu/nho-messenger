package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum AvatarType {
	ICON,
	IMAGE_FACEBOOK,
	IMAGE_UPLOAD,
	IMAGE_CAMERA;
	private static AtomicInteger codeSeed ;
	private static final int genCode(){
		if(codeSeed==null){
			codeSeed = new AtomicInteger(0);
		}
		return codeSeed.getAndIncrement();
	}
	private int code = genCode();
	public int getCode(){
		return this.code;
	}
	public static AvatarType fromCode(int code){
		for(AvatarType type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null;
	}
}
