package com.nho.message.response.chat.anything;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class ListObjectInChannelResponse extends NhoMessage {

	{
		this.setType(MessageType.GET_LIST_OBJECT_IN_CHANNEL_RESPONSE);
	}
	private boolean successful;
	private Error error;
	private List<String> objIds;
	private List<String> strokes;
	private List<Float> xs;
	private List<Float> ys;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.objIds);
			puArray.addFrom(this.xs);
			puArray.addFrom(this.ys);
			puArray.addFrom(this.strokes);
		} else {
			puArray.addFrom(this.error == null ? null : this.error.getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			List<String> objIds = new ArrayList<>();
			PuArray objIdArray = puArray.remove(0).getPuArray();
			if (objIdArray != null) {
				for (PuValue value : objIdArray) {
					objIds.add(value.getString());
				}
			}
			this.objIds = objIds;

			

			List<Float> xs = new ArrayList<>();
			PuArray xArray = puArray.remove(0).getPuArray();
			if (xArray != null) {
				for (PuValue value : xArray) {
					xs.add(value.getFloat());
				}
			}
			this.xs = xs;

			List<Float> ys = new ArrayList<>();
			PuArray yArray = puArray.remove(0).getPuArray();
			if (yArray != null) {
				for (PuValue value : yArray) {
					ys.add(value.getFloat());
				}
			}
			this.ys = ys;
			
			List<String> strokes = new ArrayList<>();
			PuArray strokeArray = puArray.remove(0).getPuArray();
			if (strokeArray != null) {
				for (PuValue value : strokeArray) {
					strokes.add(value.getString());
				}
			}
			this.strokes = strokes;
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

	public List<String> getObjIds() {
		return objIds;
	}

	public void setObjIds(List<String> objIds) {
		this.objIds = objIds;
	}

	public List<String> getStrokes() {
		return strokes;
	}

	public void setStrokes(List<String> strokes) {
		this.strokes = strokes;
	}

	public List<Float> getXs() {
		return xs;
	}

	public void setXs(List<Float> xs) {
		this.xs = xs;
	}

	public List<Float> getYs() {
		return ys;
	}

	public void setYs(List<Float> ys) {
		this.ys = ys;
	}

}
