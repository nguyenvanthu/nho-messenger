package com.nho.message.response.chat.anything;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;

public class CanMoveLiveObjectResponse extends NhoMessage{
	{
		this.setType(MessageType.CAN_MOVE_LIVE_OBJ_RESPONSE);
	}
	private boolean isCanMove ;
	private String objId ;
	private Error error ;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.isCanMove);
		puArray.addFrom(this.objId);
		puArray.addFrom(this.error == null ? null : this.error.getCode());
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.isCanMove = puArray.remove(0).getBoolean();
		this.objId = puArray.remove(0).getString();
		PuValue error = puArray.remove(0);
		if(error != null && error.getType() != PuDataType.NULL){
			this.error = Error.fromCode(error.getInteger());
		}
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public boolean isCanMove() {
		return isCanMove;
	}

	public void setCanMove(boolean isCanMove) {
		this.isCanMove = isCanMove;
	}
	
	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}
	
}
