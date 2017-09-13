package com.nho.message.response.channel;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class LeaveChannelResponse extends NhoMessage {

	{
		this.setType(MessageType.LEAVE_CHANNEL_RESPONSE);
	}

	private boolean successful;
	private String channelId;
	private String userName ;
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private Error error;

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Override
	protected void writePuArray(PuArray puArray) { 
		puArray.addFrom(this.successful);
		if(this.successful){
			puArray.addFrom(this.channelId);
			puArray.addFrom(this.userName);
		}else {
			puArray.addFrom(this.error == null ? null : this.error.getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if(this.successful){
			this.channelId = puArray.remove(0).getString();
			this.userName = puArray.remove(0).getString();
		}else {
			PuValue errorValue = puArray.remove(0);
			if (errorValue != null && errorValue.getType() != PuDataType.NULL) {
				this.error = Error.fromCode(errorValue.getInteger());
			}
		}
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
}
