package com.nho.server.processors;

import com.nhb.common.BaseLoggable;
import com.nho.server.NhoServer;

public abstract class AbstractProcessor extends BaseLoggable {
	private NhoServer context ;

	public NhoServer getContext() {
		return context;
	}

	public void setContext(NhoServer context) {
		this.context = context;
	}
	
	protected abstract void process(); 
}
