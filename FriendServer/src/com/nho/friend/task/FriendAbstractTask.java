package com.nho.friend.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.common.Loggable;
import com.nhb.common.annotations.Transparent;
import com.nho.friend.FriendHandler;

public abstract class FriendAbstractTask extends SchedulerTask implements Loggable{
	private Logger logger = null;
	private FriendHandler context ;
	
	@Override
	@Transparent
	public Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(getClass());
		}
		return logger;
	}

	@Override
	@Transparent
	public Logger getLogger(String name) {
		return LoggerFactory.getLogger(name);
	}

	public FriendHandler getContext() {
		return context;
	}

	public void setContext(FriendHandler context) {
		this.context = context;
	}
}
