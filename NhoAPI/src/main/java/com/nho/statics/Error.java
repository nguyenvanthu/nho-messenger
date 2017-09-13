package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum Error {

	OK, USER_NOT_LOGGED_IN, USER_ALREADY_IN_CHANNEL, USER_NOT_JOINED_TO_CHANNEL, CHANNEL_NOT_FOUND, CHANNEL_FULL, INVALID_LOGIN, CREATE_ACCOUNT_ERROR, CHANNEL_TYPE_INVALID, INVITED_USER_INVALID, SEND_FRIEND_REQUEST_ERROR, USER_NOT_EXIST, ACCEPT_FRIEND_ERROR, IGNORE_FRIEND_ERROR, BLOCK_FRIEND_ERROR, CANCEL_FRIEND_ERROR, ACCOUNT_NOT_EXIST, UNBLOCK_FRIEND_ERROR, CREATE_APPLICATION_ERROR, REGISTER_PUSH_NOTIFICATION_ERROR, ACCOUNT_NOT_ENOUGH_INFORMATION, USER_BLOCKED, ALREADY_SEND_FRIEND_REQUEST, ALREADY_BLOCKED_ERROR, CANNOT_BLOCK_FRIEND_ERROR, NETWORK_DISCONNECT, INVALID_PASSWORD_OR_USERNAME, USER_ALREADY_REGISTER, ACCOUNT_EXIST, NON_FRIEND, USER_OFFLINE, SEND_FRIEND_REQUEST_NOT_ENOUGH_INFO, SEND_FRIEND_REQUEST_ITSELF, ALREADY_FRIEND, RECEIVER_BUZY, ERROR_REGISTER_PUSH, USER_LOGOUT, INVALID_TOKEN, ERROR_RABBIT_MQ, FACE_TOKEN_NOT_FOUND, FACE_TOKEN_EXPIRED, FACE_ID_NOT_FOUND, UPDATE_FAIL;

	private static AtomicInteger codeSeed;

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

	public static Error fromCode(int code) {
		for (Error error : values()) {
			if (error.getCode() == code) {
				return error;
			}
		}
		return null;
	}
}