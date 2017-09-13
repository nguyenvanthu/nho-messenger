package com.nho.message.response.chat.anything;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class ReleaseObjResponse extends NhoMessage {
	{
		this.setType(MessageType.RELEASE_OBJ_RESPONSE);
	}

	private String objId;
	private String userName;
	private float x;
	private float y;
	private long timeStamp;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.objId);
		puArray.addFrom(this.userName);
		puArray.addFrom(this.x);
		puArray.addFrom(this.y);
		puArray.addFrom(this.timeStamp);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.objId = puArray.remove(0).getString();
		this.userName = puArray.remove(0).getString();
		this.x = puArray.remove(0).getFloat();
		this.y = puArray.remove(0).getFloat();
		this.timeStamp = puArray.remove(0).getLong();
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
