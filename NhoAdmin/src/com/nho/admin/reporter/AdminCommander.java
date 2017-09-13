package com.nho.admin.reporter;

import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nho.admin.NhoAdminHandler;

public class AdminCommander extends BaseEventDispatcher{
	private NhoAdminHandler context ;
	public AdminCommander(NhoAdminHandler context) {
		this.context = context;
		this.addEventListener(AdminEvent.REPORT_DAILY, new AdminReporter(context));
	}
	public void report(){
		getLogger().debug("Admin dispatch event report daily");
		dispatchEvent(AdminEvent.createDailyReportEvent());
	}
	public NhoAdminHandler getContext() {
		return context;
	}
	public void setContext(NhoAdminHandler context) {
		this.context = context;
	}
}
