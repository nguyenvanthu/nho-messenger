package com.nho.admin.router;

import com.nhb.common.BaseLoggable;
import com.nho.admin.NhoAdminHandler;

public abstract class AdminAbstractProcessor extends BaseLoggable implements AdminProcessor{
	private NhoAdminHandler context ;

	public NhoAdminHandler getContext() {
		return context;
	}

	public void setContext(NhoAdminHandler context) {
		this.context = context;
	}
	
}
