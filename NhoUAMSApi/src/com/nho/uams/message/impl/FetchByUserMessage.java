package com.nho.uams.message.impl;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.UAMSApiField;

public class FetchByUserMessage extends UAMSAbstractMessage {
	private static final long serialVersionUID = 1L;
	{
		this.setType(UAMSMessageType.FETCH_BY_USER);
	}
	private String userName;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(this.userName);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.userName = array.remove(0).getString();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public static FetchByUserMessage fromPuObject(PuObject data) {
		FetchByUserMessage message = new FetchByUserMessage();
		data.setType(UAMSApiField.APPLICATION_ID, PuDataType.STRING);
		data.setType(UAMSApiField.USERNAME, PuDataType.STRING);

		message.setApplicationId(data.getString(UAMSApiField.APPLICATION_ID));
		message.setUserName(data.getString(UAMSApiField.USERNAME));

		return message;
	}
}
