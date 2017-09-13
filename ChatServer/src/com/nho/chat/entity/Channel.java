//package com.nho.chat.entity;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//import org.bson.types.ObjectId;
//
//import com.nhb.eventdriven.impl.BaseEventDispatcher;
//import com.nho.statics.ChannelType;
//
//public class Channel extends BaseEventDispatcher {
//
//	private int type = 1;
//	private final ObjectId id;
//	private String channelId ;
//
//	public String getChannelId() {
//		return channelId;
//	}
//
//	public void setChannelId(String channelId) {
//		this.channelId = channelId;
//	}
//
//	private final Set<String> subscribers = new CopyOnWriteArraySet<>();
//
//	public Channel(ObjectId id) {
//		this.id = id;
//	}
//
//	public Channel() {
//		this(new ObjectId());
//	}
//
//	public ChannelType getType() {
//		return ChannelType.fromCode(type);
//	}
//
//	public void setType(ChannelType type) {
//		this.type = type.getCode();
//	}
//
//	public ObjectId getId() {
//		return id;
//	}
//
//	public Set<String> getSubscribers() {
//		
//		return new HashSet<>(subscribers);
//	}
//
//	public boolean addSubscriber(String... subscribers) {
//		return this.subscribers.addAll(Arrays.asList(subscribers));
//	}
//
//	public boolean removeSubscriber(String subscriber) {
//		return this.subscribers.remove(subscriber);
//	}
//}
