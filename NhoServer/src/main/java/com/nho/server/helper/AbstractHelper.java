package com.nho.server.helper;

import com.nhb.common.BaseLoggable;
import com.nho.server.NhoServer;

public class AbstractHelper extends BaseLoggable{
	private NhoServer context ;

	public NhoServer getContext() {
		return context;
	}

	public void setContext(NhoServer context) {
		this.context = context;
	}
}
