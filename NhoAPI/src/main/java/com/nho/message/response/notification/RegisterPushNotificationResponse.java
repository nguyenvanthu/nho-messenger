package com.nho.message.response.notification;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class RegisterPushNotificationResponse extends NhoMessage {

	{
		this.setType(MessageType.REGISTER_PUSH_NOTIFICATION_RESPONSE);
	}

	private boolean success;
	private int status;
	private Error error;
	private String deviceTokenId;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDeviceTokenId() {
		return deviceTokenId;
	}

	public void setDeviceTokenId(String deviceTokenId) {
		this.deviceTokenId = deviceTokenId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.success);
		if (this.success) {
			puArray.addFrom(this.status);
			puArray.addFrom(this.deviceTokenId);
		} else {
			puArray.addFrom(this.status);
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}

	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
		if (this.success) {
			this.status = puArray.remove(0).getInteger();
			this.deviceTokenId = puArray.remove(0).getString();
		} else {
			this.status = puArray.remove(0).getInteger();
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
