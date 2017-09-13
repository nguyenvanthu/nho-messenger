package com.nho.statics;

import java.io.Serializable;

public enum Gender implements Serializable {

	UNKNOWN(-1), FEMALE(0), MALE(1), GAY(2), LESBIAN(3), BISEXUAL(4), TRANSGENDER(5);

	private final int id;

	private Gender(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static Gender fromId(int gender) {
		for (Gender val : values()) {
			if (val.getId() == gender) {
				return val;
			}
		}
		return UNKNOWN;
	}
}
