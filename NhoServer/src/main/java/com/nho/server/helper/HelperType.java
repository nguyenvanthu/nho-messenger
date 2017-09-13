package com.nho.server.helper;

import java.util.concurrent.atomic.AtomicInteger;

public enum HelperType {
	CHANNEL,
	NOTIFICATION,
	FRIEND,
	LOGGING;
	private static AtomicInteger codeSeed ;
	public static int genCode(){
		if(codeSeed == null ){
			codeSeed = new AtomicInteger(0);
		}
		return codeSeed.getAndIncrement();
	}
	private int code = genCode();
	public int getCode(){
		return this.code;
	}
	public static HelperType fromCode(int code){
		for(HelperType type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null;
	}
}
