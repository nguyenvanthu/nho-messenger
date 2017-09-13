package com.nho.friend.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum FriendCommand {
	ACCEPT_FRIEND, 
	BLOCK_FRIEND, 
	CANCEL_FRIEND,
	GET_LIST_PENDDING, 
	GET_LIST_FRIEND, 
	GET_LIST_INVITED,
	IGNORE_FRIEND,
	SEND_FRIEND_REQUEST, 
	UNBLOCK_FRIEND,
	MAKE_FRIEND,
	MAKE_FRIEND_WITH_BOT, 
	UPDATE_FRIEND_DB, DELETE_FRIEND;

	private static AtomicInteger codeSeed = new AtomicInteger(0);

	private static final int genCode() {
		if (codeSeed == null) {
			codeSeed = new AtomicInteger(0);
		}
		return codeSeed.getAndIncrement();
	}

	private int code = genCode();

	public int getCode() {
		return this.code;
	}

	public static FriendCommand fromCode(int code) {
		for (FriendCommand command : values()) {
			if (command.getCode() == code) {
				return command;
			}
		}
		return null;
	}

}
