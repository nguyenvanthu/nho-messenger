package com.nho.statics;

import java.util.concurrent.atomic.AtomicInteger;

public enum Theme {
	TEAL, BLUE, DEEP_PERPLE, PINK, ORANGE, LIGHT_GREEN;
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

	public static Theme fromCode(int code) {
		for (Theme theme : values()) {
			if (theme.getCode() == code) {
				return theme;
			}
		}
		return null;
	}
}
