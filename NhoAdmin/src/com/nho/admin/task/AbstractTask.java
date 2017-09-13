package com.nho.admin.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.common.Loggable;
import com.nhb.common.annotations.Transparent;
import com.nho.admin.NhoAdminHandler;

public abstract class AbstractTask extends SchedulerTask implements Loggable{
	private NhoAdminHandler context ;
	private Logger logger = null;
	
	public NhoAdminHandler getContext() {
		return context;
	}

	public void setContext(NhoAdminHandler context) {
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
