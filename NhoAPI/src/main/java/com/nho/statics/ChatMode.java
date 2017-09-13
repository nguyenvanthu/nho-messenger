package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum ChatMode {
	DEFAULT,
	LIVECHAT;
	private static AtomicInteger seed = null ;
	private static int genCode(){
		if(seed == null ){
			seed = new AtomicInteger(0);
		}
		return seed.getAndIncrement();
	}
	private int code  = genCode();
	public int getCode(){
		return this.code;
	}
	
	public static ChatMode fromCode(int code){
		for (ChatMode mode : values()){
			if(mode.getCode() == code){
				return mode ;
			}
		}
		return null;
	}
}
