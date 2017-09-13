package com.nho.server.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum MessageSenderType {
	EMAIL,
	SMS;
	
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
	public static MessageSenderType fromCode(int code){
		for(MessageSenderType type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null;
	}
}
