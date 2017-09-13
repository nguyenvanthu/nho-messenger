package com.nho.statics;

public enum ChannelType {

	PRIVATE(1, 2), PUBLIC(2, -1);

	private int code;
	private int maxSubscriber;

	private ChannelType(int code, int max) {
		this.code = code;
		this.maxSubscriber = max;
	}

	public int getCode() {
		return code;
	}

	public int getMaxSubscriber() {
		return this.maxSubscriber;
	}

	public static ChannelType fromCode(int type) {
		for (ChannelType ct : values()) {
			if (ct.getCode() == type) {
				return ct;
			}
		}
		return null;
	}
}
