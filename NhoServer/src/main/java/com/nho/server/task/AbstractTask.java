package com.nho.server.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.common.Loggable;
import com.nhb.common.annotations.Transparent;
import com.nho.server.NhoServer;
import com.nho.server.data.UserMongoModel;

public abstract class AbstractTask extends SchedulerTask implements Loggable{
	private NhoServer context ;
	private Logger logger = null;
	
	private UserMongoModel userMongoModel;

	protected UserMongoModel getUserMongoModel() {
		if (this.userMongoModel == null) {
			this.userMongoModel = getContext().getModelFactory().newModel(UserMongoModel.class);
		}
		return this.userMongoModel;
	}
	
	public NhoServer getContext() {
		return context;
	}

	public void setContext(NhoServer context) {
		this.context = context;
	}
	
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
}
