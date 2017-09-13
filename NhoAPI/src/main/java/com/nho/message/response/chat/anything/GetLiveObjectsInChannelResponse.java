package com.nho.message.response.chat.anything;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class GetLiveObjectsInChannelResponse extends NhoMessage {
	{
		this.setType(MessageType.GET_LIVE_OBJECTS_IN_CHANNEL);
	}

	private boolean successful;
	private Error error;
	private List<String> liveObjects;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.liveObjects);
		} else {
			puArray.addFrom(this.error == null ? null : this.error.getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			List<String> liveObjects = new ArrayList<>();
			PuArray liveObjectArray = puArray.remove(0).getPuArray();
			if (liveObjectArray != null) {
				for (PuValue value : liveObjectArray) {
					liveObjects.add(value.getString());
				}
			}
			this.liveObjects = liveObjects;
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public List<String> getLiveObjects() {
		return liveObjects;
	}

	public void setLiveObjects(List<String> liveObjects) {
		this.liveObjects = liveObjects;
	}

}
