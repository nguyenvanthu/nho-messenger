package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum AppState {
	PAUSE,
	RESUME;
	private static AtomicInteger codeSeed = new AtomicInteger(0);
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
	public static AppState fromCode(int code){
		for(AppState type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null;
	}
}
