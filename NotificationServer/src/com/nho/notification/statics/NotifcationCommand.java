package com.nho.notification.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum NotifcationCommand {
	PUSH_BY_DEVICE_TOKEN, PUSH_BY_USERNAME, REGISTER_TOKEN, REMOVE_TOKEN, UPDATE_DEVICE_TOKEN_DB;
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

	public static NotifcationCommand fromCode(int code) {
		for (NotifcationCommand command : values()) {
			if (command.getCode() == code) {
				return command;
			}
		}
		return null;
	}
}
