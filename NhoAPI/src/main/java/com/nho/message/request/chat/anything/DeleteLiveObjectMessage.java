package com.nho.message.request.chat.anything;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class DeleteLiveObjectMessage extends NhoMessage implements Request{
	{
		this.setType(MessageType.DELETE_LIVE_OBJECT);
	}
	private String channelId ;
	private String from ;
	private List<String> objIds ;
	private PuObject data ;
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.channelId);
		puArray.addFrom(this.from);
		puArray.addFrom(this.objIds);
		puArray.addFrom(this.data);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.channelId = puArray.remove(0).getString();
		this.from = puArray.remove(0).getString();
		PuArray array = puArray.remove(0).getPuArray();
		if (array != null) {
			this.objIds = new ArrayList<String>();
			for (PuValue value : array) {
				this.objIds.add(value.getString());
			}
		}
		this.data = puArray.remove(0).getPuObject();
	}
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public List<String> getObjIds() {
		return objIds;
	}
	public void setObjIds(List<String> objIds) {
		this.objIds = objIds;
	}
	public PuObject getData() {
		return data;
	}
	public void setData(PuObject data) {
		this.data = data;
	}
}
