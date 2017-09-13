package com.nho.tracking.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum ExceptionType {
	ID, FRIEND, CHAT, CHANNEL, BOT, LIVE_CHAT, NOTIFICATION, SCHEDULED;
	private static AtomicInteger seed = null;

	private static int genCode() {
		if (seed == null) {
			seed = new AtomicInteger(0);
		}
		return seed.getAndIncrement();
	}

	private int code = genCode();

	public int getCode() {
		return this.code;
	}

	public static ExceptionType fromCode(int code) {
		for (ExceptionType type : values()) {
			if (type.getCode() == code) {
				return type;
			}
		}
		return null;
	}
}
