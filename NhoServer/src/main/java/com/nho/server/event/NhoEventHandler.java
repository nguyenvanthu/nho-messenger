package com.nho.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.common.Loggable;
import com.nhb.common.annotations.Transparent;
import com.nhb.eventdriven.impl.BaseEventHandler;
import com.nho.server.NhoServer;
import com.nho.server.data.UserMongoModel;

public abstract class NhoEventHandler extends BaseEventHandler implements Loggable {
	private NhoServer context;
	private Logger logger = null;
	private UserMongoModel userMongoModel;

	protected UserMongoModel getUserMongoModel() {
		if (this.userMongoModel == null) {
			this.userMongoModel = getContext().getModelFactory().newModel(UserMongoModel.class);
		}
		return this.userMongoModel;
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

	protected NhoServer getContext() {
		return this.context;
	}

	public void setContext(NhoServer context) {
		this.context = context;
	}
}
