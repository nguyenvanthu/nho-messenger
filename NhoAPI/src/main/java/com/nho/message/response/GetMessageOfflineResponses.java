package com.nho.message.response;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.Error;

public class GetMessageOfflineResponses extends NhoMessage implements Request {
	{
		this.setType(MessageType.GET_MESSAGE_OFFLINE_RESPONSE);
	}

	private boolean successful;
	private List<PuObject> messageOfflines;
	private Error error;

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public List<PuObject> getMessageOfflines() {
		return messageOfflines;
	}

	public void setMessageOfflines(List<PuObject> messageOfflines) {
		this.messageOfflines = messageOfflines;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.messageOfflines);
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			PuArray messages = puArray.remove(0).getPuArray();
			if (messages != null) {
				messageOfflines = new ArrayList<>();
				for (PuValue message : messages) {
					this.messageOfflines.add(message.getPuObject());
				}
			}
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
