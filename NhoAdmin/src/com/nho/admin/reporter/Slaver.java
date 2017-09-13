package com.nho.admin.reporter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.nho.admin.NhoAdminHandler;
import com.nho.admin.statics.Timer;
import com.nho.admin.task.DailyReportTask;

public class Slaver {
	private NhoAdminHandler context;
	private static final String TIME_ZONE = "Asia/Saigon";

	public Slaver(NhoAdminHandler context) {
		this.context = context;
	}

	private long getDelayTime() {
		long time = 0;
		DateTime date = new DateTime().withZone(DateTimeZone.forID(TIME_ZONE));
		int currentHour = date.getHourOfDay();
		if (currentHour < 6) {
			DateTime nextTime = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 6, 0,
					DateTimeZone.forID(TIME_ZONE));
			time = nextTime.getMillis() - date.getMillis();
		} else {
			DateTime nextDay = date.plusDays(1);
			DateTime nextTime = new DateTime(nextDay.getYear(), nextDay.getMonthOfYear(), nextDay.getDayOfMonth(), 6, 0,
					DateTimeZone.forID(TIME_ZONE));
			time = nextTime.getMillis() - date.getMillis();
		}
		return time;
	}

	public void report() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				DailyReportTask task = new DailyReportTask(getContext());
				task.run();
			}
		}, getDelayTime(), Timer.PERIOD, TimeUnit.MILLISECONDS);
	}

	public NhoAdminHandler getContext() {
		return context;
	}

	public void setContext(NhoAdminHandler context) {
		this.context = context;
	}
}
