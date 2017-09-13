package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum GameType {
	XO,
	MAZE;
	private static AtomicInteger seed = null;
	private static int genCode(){
		if(seed == null ){
			seed = new AtomicInteger(0);
		}
		return seed.getAndIncrement();
	}
	
	private int code = genCode();
	
	public int getCode(){
		return this.code;
	}
	public static GameType fromCode(int code ){
		for(GameType type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null ;
	}
}
