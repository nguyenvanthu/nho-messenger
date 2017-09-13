package com.nho.admin.reporter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.common.Loggable;
import com.nhb.common.annotations.Transparent;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.impl.BaseEventHandler;
import com.nho.admin.NhoAdminHandler;

public class AdminReporter extends BaseEventHandler implements Loggable {
	private Logger logger = null;
	private NhoAdminHandler context ;
	public AdminReporter(NhoAdminHandler context) {
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

	@Override
	public void onEvent(Event event) throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			Slaver slaver = new Slaver(getContext());
			slaver.report();
		});
	}

	public NhoAdminHandler getContext() {
		return context;
	}

	public void setContext(NhoAdminHandler context) {
		this.context = context;
	}
}
