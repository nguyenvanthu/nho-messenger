package com.nho.uams.message;

import java.io.Serializable;

import com.nhb.common.data.PuArray;

public interface UAMSMessage extends Serializable {
	UAMSMessageType getType();

	PuArray serialize();

	String getApplicationId();
}
