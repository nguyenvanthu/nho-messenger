package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum StatusUser {
	ONLINE ,
	BUSY,
	OFFLINE,
	LOGOUT;
	private static AtomicInteger codeSeed = new AtomicInteger();
	private static int genCode(){
		if(codeSeed == null){
			codeSeed = new AtomicInteger(0);
		}
		return codeSeed.getAndIncrement();
	}
	private int code = genCode();
	public int getCode(){
		return this.code;
	}
	public static StatusUser fromCode(int code){
		for(StatusUser status : values()){
			if(status.getCode() == code ){
				return status ;
			}
		}
		return null;
	}
}
