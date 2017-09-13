package com.nho.message.response.chat.anything;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class MakeObjectCompleteResponse extends NhoMessage {
	{
		this.setType(MessageType.MAKE_OBJECT_CHAT_RESPONSE);
	}

	private String from;
	private String channelId;
	private int startId;
	private int endId;
	private String objId;
	private String data = "";
	private String dataType ="";
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.from);
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.objId);
		puArray.addFrom(this.startId);
		puArray.addFrom(this.endId);
		puArray.addFrom(this.data);
		puArray.addFrom(this.getDataType());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.from = puArray.remove(0).getString();
		this.channelId = puArray.remove(0).getString();
		this.objId = puArray.remove(0).getString();
		this.startId = puArray.remove(0).getInteger();
		this.endId = puArray.remove(0).getInteger();
		this.data = puArray.remove(0).getString();
		this.setDataType(puArray.remove(0).getString());
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public int getStartId() {
		return startId;
	}

	public void setStartId(int startId) {
		this.startId = startId;
	}

	public int getEndId() {
		return endId;
	}

	public void setEndId(int endId) {
		this.endId = endId;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
