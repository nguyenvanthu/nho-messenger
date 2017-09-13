package com.nho.admin.statics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeConverter {
	public static String getDate(long time) {
		String dateFormat = "dd/MM/yyyy hh:mm:ss";
		SimpleDateFormat formater = new SimpleDateFormat(dateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return formater.format(calendar.getTime());
	}

	public static long getTimeOfDate(int day, int month, int year) {
		long time = -1;
		String source = year + "-" + month + "-" + day;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = format.parse(source);
			time = date.getTime();
			System.out.println("time in milisecond: " + time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
}
