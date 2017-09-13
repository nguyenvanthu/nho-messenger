package com.nho.uams.message.impl;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.UAMSApiField;

public class FetchByReferenceMessage extends UAMSAbstractMessage {
	private static final long serialVersionUID = 1L;
	{
		this.setType(UAMSMessageType.FETCH_BY_REFERENCE);
	}
	private String referenceId;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(this.referenceId);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.referenceId = array.remove(0).getString();
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public static FetchByReferenceMessage fromPuObject(PuObject data) {
		FetchByReferenceMessage message = new FetchByReferenceMessage();
		data.setType(UAMSApiField.APPLICATION_ID, PuDataType.STRING);
		data.setType(UAMSApiField.REFERENCE_ID, PuDataType.STRING);

		message.setApplicationId(data.getString(UAMSApiField.APPLICATION_ID));
		message.setReferenceId(data.getString(UAMSApiField.REFERENCE_ID));
		return message;
	}
}
