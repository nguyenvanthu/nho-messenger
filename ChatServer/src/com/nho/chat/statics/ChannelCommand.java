package com.nho.chat.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum ChannelCommand {
	ADD_LIVE_OBJECT,
	ADD_STROKE,
	CAN_MOVE_OBJECT,
	CHECK_CHANNEL,
	CHECK_RECEIVER_BUSY,
	CHECK_USER_BUSY,
	CREATE_CHANNEL,
	CREATE_CHANNEL_WITH_BOT,
	DELTE_LIVE_OBJ,
	DELETE_OBJ,
	GET_LIST_USER_BUSY,
	GET_CHANNEL_BY_USER,
	GET_CHANNEL,
	GET_LIST_OBJ_CHANNEL,
	GET_TIME_CHAT_WITH_BOT,
	GET_WAIT_TIME,
	JOIN_CHANNEL,
	LEAVE_CHANNEL,
	RELEASE_OBJECT,
	REMOVE_OBJ_BY_USER,
	REMOVE_TIME,
	REMOVE_USER_CHANNEL,
	REMOVE_WAIT_TIME,
	RESET_WAIT_TIME,
	STATE_APP_CHANGE,
	STORE_TIME,
	UPDATE_POSITION,
	UPDATE_STATUS,
	UPDATE_WAIT_TIME,
	STORE_DATA_MSG,
	GET_DATA_MSG_BOT,
	REMOVE_DATA_MSG_BOT,
	UPDATE_CHANNEL,
	SORT_FRIEND_BY_TIME,
	UPDATE_PING_TIMES,
	GET_PING_TIMES,
	DELETE_PING_TIMES;
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
	public static ChannelCommand fromCode(int code){
		for(ChannelCommand command : values()){
			if(command.getCode() == code){
				return command;
			}
		}
		return null;
	}
}
