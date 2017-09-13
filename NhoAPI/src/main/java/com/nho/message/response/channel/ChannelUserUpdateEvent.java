//package com.nho.message.response.channel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.nhb.common.data.PuArray;
//import com.nhb.common.data.PuObject;
//import com.nhb.common.data.PuValue;
//import com.nho.message.MessageType;
//import com.nho.message.NhoMessage;
//
//public class ChannelUserUpdateEvent extends NhoMessage {
//
//	{
//		this.setType(MessageType.CHANNEL_USER_UPDATE);
//	}
//
//	private String channelId;
//	private int action; // 1 -> added, -1 -> removed, 0 -> update all
//	private int userCount;
//	private List<String> profileIds;
//	private List<PuObject> basicProfiles;
//
//	public int getAction() {
//		return action;
//	}
//
//	public void setAction(int action) {
//		if (action != -1 && action != 0 && action != 1) {
//			throw new IllegalArgumentException("Action must be one of among {-1, 0, 1}");
//		}
//		this.action = action;
//	}
//
//	@Override
//	protected void writePuArray(PuArray puArray) {
//		puArray.addFrom(this.channelId);
//		puArray.addFrom(this.userCount);
//		puArray.addFrom(this.action);
//		puArray.addFrom(this.profileIds);
//		puArray.addFrom(this.basicProfiles);
//	}
//
//	@Override
//	protected void readPuArray(PuArray puArray) {
//		this.channelId = puArray.remove(0).getString();
//		this.userCount = puArray.remove(0).getInteger();
//		this.action = puArray.remove(0).getInteger();
//		PuArray arr = puArray.remove(0).getPuArray();
//		if (arr != null) {
//			this.profileIds = new ArrayList<>();
//			for (PuValue value : arr) {
//				this.profileIds.add(value.getString());
//			}
//		}
//		arr = puArray.remove(0).getPuArray();
//		if (arr != null) {
//			this.basicProfiles = new ArrayList<>();
//			for (PuValue value : arr) {
//				this.basicProfiles.add(value.getPuObject());
//			}
//		}
//	}
//
//	public String getChannelId() {
//		return channelId;
//	}
//
//	public void setChannelId(String channelId) {
//		this.channelId = channelId;
//	}
//
//	public int getUserCount() {
//		return userCount;
//	}
//
//	public void setUserCount(int userCount) {
//		this.userCount = userCount;
//	}
//
//	public List<PuObject> getBasicProfiles() {
//		return basicProfiles;
//	}
//
//	public void setBasicProfiles(List<PuObject> basicProfiles) {
//		this.basicProfiles = basicProfiles;
//	}
//
//	public List<String> getProfileIds() {
//		return profileIds;
//	}
//
//	public void setProfileIds(List<String> profileIds) {
//		this.profileIds = profileIds;
//	}
//}
