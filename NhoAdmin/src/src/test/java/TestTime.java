package src.test.java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TestTime {
	public static void main(String[] args) {
//		getTimeOfDate(20, 12, 2016);
		getTime();
	}

	public static long getTimeOfDate(int day,int month,int year){
		long time = 0;
		String source = year+"-"+month+"-"+day;
		System.out.println(source);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = format.parse(source);
			time = date.getTime();
			System.out.println("time in milisecond: "+time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
	
	public static long getTime(){
		long time = 0;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Saigon"));
		Date currentDate = calendar.getTime();
		System.out.println(currentDate.toString());
		
		DateTime date = new DateTime().withZone(DateTimeZone.forID("Asia/Saigon"));
		DateTime nextDay = date.plusDays(1);
		System.out.println(nextDay.getHourOfDay());
		DateTime nextTime = new DateTime(nextDay.getYear(), nextDay.getMonthOfYear(), nextDay.getDayOfMonth(), 6, 0,
				DateTimeZone.forID("Asia/Saigon"));
		System.out.println(nextTime.getHourOfDay());
		System.out.println(nextTime.getDayOfMonth());
		System.out.println(nextTime.getMillis()-date.getMillis());
		return time;
	}
}
