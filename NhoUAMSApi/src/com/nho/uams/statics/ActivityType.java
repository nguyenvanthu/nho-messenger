package com.nho.uams.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum ActivityType {
	REGISTER,
	LOGIN,
	DISCONECT,
	LOGOUT,
	UPDATE_PROFILE,
	CHANGE_SETTING,
	
	MAKE_FRIEND_WHEN_LOGIN,
	SEND_FRIEND_REQUEST,
	ACCEPT_FRIEND,
	DELETE_FRIEND,
	
	CREATE_CHANNEL_WITH_BOT,
	CREATE_CHANNEL_WITH_FRIEND,
	CREATE_CHANNEL_FAIL,
	LEAVE_CHANNEL,
	
	POKE,
	MAKE_LIVE_OBJECT,
	RELEASE_LIVE_OBJECT,
	DELETE_LIVE_OBJECT,
	SEND_STICKER,
	PLAY_GAME,
	LEAVE_CHANNEL_WITH_BOT;
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
	public static ActivityType fromCode(int code){
		for(ActivityType type : values()){
			if(type.getCode() == code){
				return type;
			}
		}
		return null;
	}
}
