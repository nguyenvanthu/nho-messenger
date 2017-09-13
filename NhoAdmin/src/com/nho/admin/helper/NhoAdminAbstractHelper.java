package com.nho.admin.helper;

import com.nhb.common.BaseLoggable;
import com.nho.admin.NhoAdminHandler;

public abstract class NhoAdminAbstractHelper extends BaseLoggable{
	private NhoAdminHandler context;

	public NhoAdminHandler getContext() {
		return context;
	}

	public void setContext(NhoAdminHandler context) {
		this.context = context;
	}
}
