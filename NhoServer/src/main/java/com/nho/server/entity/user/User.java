package com.nho.server.entity.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.statics.StatusUser;

public class User extends BaseEventDispatcher implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String userName;
	private String listenerId;
	private StatusUser status ;
	private final transient Set<String> sessions = new CopyOnWriteArraySet<>();

	private final transient Set<String> cachedSubscribedChannels = new CopyOnWriteArraySet<>();
	private transient ISet<String> subscribedChannels;

	private ItemListener<String> subscribedChannelsListener = new ItemListener<String>() {

		@Override
		public void itemRemoved(ItemEvent<String> event) {
			cachedSubscribedChannels.remove(event.getSource());
			dispatchEvent(UserEvent.createUnsubscribedOnChannelEvent(event.getItem()));
		}

		@Override
		public void itemAdded(ItemEvent<String> event) {
			cachedSubscribedChannels.add(event.getItem());
			dispatchEvent(UserEvent.createSubscribedOnNewChannelEvent(event.getItem()));
		}
	};


	public User(String userName, ISet<String> subscribedChannels) {
		this.userName = userName;
		this.subscribedChannels = subscribedChannels;

		this.cachedSubscribedChannels.addAll(this.subscribedChannels);
		this.listenerId = this.subscribedChannels.addItemListener(this.subscribedChannelsListener, true);
	}

	public void destroy() {
		this.subscribedChannels.removeItemListener(this.listenerId);
		this.subscribedChannels = null;
	}

	public String getUserName() {
		return userName;
	}

	public Set<String> getSubscribedChannels() {
		return cachedSubscribedChannels;
	}

	public void addSubscribedChannel(String channelId) {
		this.subscribedChannels.add(channelId);
	}

	public Set<String> getSessions() {
		return new HashSet<>(this.sessions);
	}

	public boolean addSession(String sessionId) {
		boolean result = this.sessions.add(sessionId);
		if (result) {
			this.dispatchEvent(UserEvent.createUserSessionOpenedEvent(sessionId, this.getUserName()));
		}
		return result;
	}

	public boolean removeSession(String sessionId) {
		boolean success = this.sessions.remove(sessionId);
		if (success) {
			this.dispatchEvent(UserEvent.createUserSessionClosedEvent(sessionId));
		}
		return success;
	}

	public StatusUser getStatus() {
		return status;
	}

	public void setStatus(StatusUser status) {
		this.status = status;
	}
}
