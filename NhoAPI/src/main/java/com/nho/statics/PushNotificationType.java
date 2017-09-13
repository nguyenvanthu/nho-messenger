package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum PushNotificationType {
	SEND_FRIEND_REQUEST,
	ACCEPT_FRIEND_REQUEST,
	POKE,
	CHAT_MESSAGE, 
	TEST,
	MAKE_FRIEND,
	MAKE_OBJ, 
	DRAW_LIVE_OBJ;
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
	public static PushNotificationType fromCode(int code){
		for(PushNotificationType type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null;
	}
}
