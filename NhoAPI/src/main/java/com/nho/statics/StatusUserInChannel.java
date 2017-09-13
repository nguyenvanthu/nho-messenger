package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum StatusUserInChannel {
	INVITED_USER_LOG_OUT,
	INVITED_USER_OFFLINE,
	INVITED_USER_BUSY,
	INVITED_USER_ON_PAUSE,
	INVITED_USER_AVAILABLE,
	USER_CHAT_WITH_BOT, 
	USER_JOIN_LOG_OUT,
	USER_JOIN_OFFLINE;
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
	public static StatusUserInChannel fromCode(int code){
		for(StatusUserInChannel type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null;
	}
}
