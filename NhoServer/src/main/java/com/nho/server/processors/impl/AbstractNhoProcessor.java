package com.nho.server.processors.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.nhb.common.BaseLoggable;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.server.NhoServer;
import com.nho.server.data.UserModel;
import com.nho.server.data.UserMongoModel;
import com.nho.server.entity.avatar.AvatarManager;
import com.nho.server.entity.user.User;
import com.nho.server.entity.user.UserManager;
import com.nho.server.helper.AbstractHelper;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.FriendHelper;
import com.nho.server.helper.HelperType;
import com.nho.server.helper.NotificationHelper;
import com.nho.server.processors.NhoRequestProcessor;
import com.nho.statics.Error;

public abstract class AbstractNhoProcessor<RequestType extends Request> extends BaseLoggable
		implements NhoRequestProcessor {

	private NhoServer context;
	private UserModel userModel;
	private UserMongoModel userMongoModel;

	protected UserMongoModel getUserMongoModel() {
		if (this.userMongoModel == null) {
			this.userMongoModel = getContext().getModelFactory().newModel(UserMongoModel.class);
		}
		return this.userMongoModel;
	}

	protected UserModel getUserModel() {
		if (this.userModel == null) {
			this.userModel = getContext().getModelFactory().newModel(UserModel.class);
		}
		return this.userModel;
	}
	
	protected AbstractHelper getHelper(HelperType type){
		switch (type) {
		case CHANNEL:
			return new ChannelHelper(getContext());
		case NOTIFICATION:
			return new NotificationHelper(getContext());
		case FRIEND:
			return new FriendHelper(getContext());
		default:
			return null;
		}
	}

	protected NhoServer getContext() {
		return this.context;
	}

	public void setContext(NhoServer context) {
		this.context = context;
	}

	protected HazelcastInstance getHazelcast() {
		return this.getContext().getHazelcast();
	}

	protected UserManager getUserManager() {
		return this.getContext().getUserManager();
	}

	protected AvatarManager getAvatarManager() {
		return this.getContext().getAvatarManager();
	}

	protected void sendError(Error error, String details, String... sessionIds) {
		this.getContext().sendError(error, details, sessionIds);
	}

	protected void sendError(Error error, String details, Set<String> sessionIds) {
		this.getContext().sendError(error, details, sessionIds);
	}

	protected void send(NhoMessage message, String... sessionIds) {
		this.getContext().send(message, sessionIds);
	}

	public void send(NhoMessage message, Collection<String> sessionIds) {
		this.getContext().send(message, sessionIds);
	}

	public void sendMessageToUsers(NhoMessage message, String currentSessionId, Collection<String> userNames) {
		if (message == null || userNames == null || userNames.size() == 0) {
			return;
		}
		Set<String> users = new HashSet<>(userNames);
		if (users.size() > 0) {
			Set<String> sessionIds = new HashSet<>();
			for (String userName : users) {
				User user = getUserManager().getUserByUserName(userName);
				if (user != null) {
					sessionIds.addAll(user.getSessions());
				}
			}
			if (sessionIds.size() > 0) {
				if (sessionIds.contains(currentSessionId)) {
					sessionIds.remove(currentSessionId);
				}
				this.send(message, sessionIds);
			}
		}
	}
	
	public void sendToUserName(NhoMessage message,String userName){
		if(message == null || userName == null){
			return;
		}
		Set<String> sessionIds = new HashSet<>();
		User user = getUserManager().getUserByUserName(userName);
		if (user != null) {
			sessionIds.addAll(user.getSessions());
		}
		if (sessionIds.size() > 0) {
			this.send(message, sessionIds);
		}
	}
	
	public void sendToUser(NhoMessage message,User user){
		if(message == null || user == null){
			return;
		}
		if(user.getSessions().size()>0){
			this.send(message, user.getSessions());
		}
	}
	
	public void sendToUsers(NhoMessage message,Collection<User> users){
		if (message == null || users == null || users.size() == 0) {
			return;
		}
		if (users.size() > 0) {
			Set<String> sessionIds = new HashSet<>();
			for (User user : users) {
				if (user != null) {
					sessionIds.addAll(user.getSessions());
				}
			}
			this.send(message, sessionIds);
		}
	}
	

	public void sendToUserNames(NhoMessage message, Collection<String> userNames) {
		if (message == null || userNames == null || userNames.size() == 0) {
			return;
		}
		Set<String> users = new HashSet<>(userNames);
		if (users.size() > 0) {
			Set<String> sessionIds = new HashSet<>();
			for (String userName : users) {
				User user = getUserManager().getUserByUserName(userName);
				if (user != null) {
					sessionIds.addAll(user.getSessions());
				}
			}
			this.send(message, sessionIds);
		}
	}

	public void sendMessageToUsers(NhoMessage message, String currentSessionId, String... userNames) {
		if (message == null || userNames == null || userNames.length == 0) {
			return;
		}
		this.sendMessageToUsers(message, currentSessionId, Arrays.asList(userNames));
	}

	public void sendToUserNames(NhoMessage message, String... userNames) {
		if (message == null || userNames == null || userNames.length == 0) {
			return;
		}
		this.sendToUserNames(message, Arrays.asList(userNames));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(Request request)  {
		try {
			try {
				this.process((RequestType) request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (ClassCastException ex) {
			throw new RuntimeException("Request type invalid", ex);
		}
	}

	protected abstract void process(RequestType request) throws Exception;
}
