package com.nho.admin.reporter;

import com.nhb.eventdriven.impl.AbstractEvent;

public class AdminEvent extends AbstractEvent{
	public static final String REPORT_DAILY = "reportDaily";
	
	public static AdminEvent createDailyReportEvent(){
		AdminEvent event = new AdminEvent();
		event.setType(REPORT_DAILY);
		
		return event;
	}
}
