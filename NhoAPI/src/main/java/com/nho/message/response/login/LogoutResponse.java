package com.nho.message.response.login;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class LogoutResponse extends NhoMessage {
	{
		this.setType(MessageType.LOGOUT_RESPONSE);
	}
	private boolean success;
	private Error error ;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
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
		puArray.addFrom(this.getError() == null ? null :this.getError().getCode());
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
		PuValue error = puArray.remove(0);
		if(error!=null && error.getType()!= PuDataType.NULL){
			this.setError(Error.fromCode(error.getInteger()));
		}
	}
}
