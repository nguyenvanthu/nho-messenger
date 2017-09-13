package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum Personality {
	SERIOUS, XI_COOL, MUNDANE, SUCCINCT, PROACTIVE, LITERALLY;
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

	public static Personality fromCode(int code) {
		for (Personality personality : values()) {
			if (personality.getCode() == code) {
				return personality;
			}
		}
		return null;
	}
}
