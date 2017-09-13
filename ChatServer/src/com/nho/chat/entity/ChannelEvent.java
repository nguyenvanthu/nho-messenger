package com.nho.chat.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nhb.eventdriven.impl.AbstractEvent;

public class ChannelEvent extends AbstractEvent {

	public static final String CHANNEL_ADDED = "channelAdded";
	public static final String CHANNEL_REMOVED = "channelRemoved";
	public static final String CHANNEL_USER_ADDED = "channelUserAdded";
	public static final String CHANNEL_USER_REMOVED = "channelUserRemoved";

	public static ChannelEvent createChannelAddedEvent(String channelId) {
		return new ChannelEvent(CHANNEL_ADDED, channelId);
	}

	public static ChannelEvent createChannelRemovedEvent(String channelId) {
		return new ChannelEvent(CHANNEL_REMOVED, channelId);
	}

	public static ChannelEvent createUserRemovedChannelEvent(String... userNames) {
		return new ChannelEvent(CHANNEL_USER_REMOVED, userNames);
	}

	public static ChannelEvent createUserRemovedChannelEvent(Collection<String> userNames) {
		return new ChannelEvent(CHANNEL_USER_REMOVED, userNames);
	}

	public static ChannelEvent createUserAddedChannelEvent(String... userNames) {
		return new ChannelEvent(CHANNEL_USER_ADDED, userNames);
	}

	public static ChannelEvent createUserAddedChannelEvent(Collection<String> userNames) {
		return new ChannelEvent(CHANNEL_USER_ADDED, userNames);
	}

	private String channelId;
	private Set<String> userNames;
	private List<String> profileIds;

	public ChannelEvent(String type, Collection<String> userNames) {
		this.setType(type);
		this.userNames = new HashSet<String>(userNames);
	}

	public ChannelEvent(String type, String... userNames) {
		this.setType(type);
		this.userNames = new HashSet<String>(Arrays.asList(userNames));
	}

	public ChannelEvent(String type, String channelId) {
		this.setType(type);
		this.setChannelId(channelId);
	}

	public ChannelEvent(String type) {
		this.setType(type);
	}

	public Set<String> getUserNames() {
		return userNames;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public List<String> getProfileIds() {
		return this.profileIds;
	}

	public void setProfileIds(List<String> profileIds) {
		this.profileIds = profileIds;
	}

}
