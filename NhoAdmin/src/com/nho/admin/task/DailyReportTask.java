package com.nho.admin.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.nhb.common.data.PuObject;
import com.nho.admin.NhoAdminHandler;
import com.nho.admin.helper.LoggingHelper;
import com.nho.admin.statics.TimeConverter;
import com.nho.uams.message.UAMSMessageType;
import com.nho.uams.statics.ActivityType;
import com.nho.uams.statics.UAMSApiField;

public class DailyReportTask extends AbstractTask {
	private static final String EMAL_1 = "admin@phugroup.com";
	private static final String EMAIL_2 = "mr.vietanh@gmail.com";
	private static final String EMAIL_3 = "thunv@phugroup.com";
	private static final Long TIME_IN_DAY = 86400000L;

	public DailyReportTask(NhoAdminHandler context) {
		super.setContext(context);
	}

	@Override
	public void run() {
		getLogger().debug("start tracking status system...");
		List<ActivityType> activityTypes = new ArrayList<>();
		activityTypes.add(ActivityType.CREATE_CHANNEL_WITH_FRIEND);
		activityTypes.add(ActivityType.CREATE_CHANNEL_FAIL);
		activityTypes.add(ActivityType.CREATE_CHANNEL_WITH_BOT);

		LoggingHelper helper = new LoggingHelper(getContext());
		String content = "Status system in " + TimeConverter.getDate(System.currentTimeMillis()) + " \n";
		for (ActivityType type : activityTypes) {
			PuObject result = helper
					.getLogFromUAMS(getMessage(type.getCode(), getStartTime(), getStartTime() + TIME_IN_DAY));
			content += "number activity " + type + " : " + result.getLong(UAMSApiField.COUNT)+" \n";
			getLogger().debug("number activity " + type + " : " + result.getLong(UAMSApiField.COUNT)+" \n");
		}
		try {
			this.sendMail(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long getStartTime() {
		long currentTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		return TimeConverter.getTimeOfDate(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.YEAR));
	}

	private PuObject getMessage(int activityType, long startTime, long endTime) {
		PuObject data = new PuObject();
		data.setInteger(UAMSApiField.MESSAGE_TYPE, UAMSMessageType.GET_COUNT_BY_ACTIVITY_TIME.getId());
		data.setInteger(UAMSApiField.ACTIVITY_TYPE, activityType);
		data.setString(UAMSApiField.APPLICATION_ID, this.getContext().getApplicationId());
		data.setLong(UAMSApiField.START_TIME, startTime);
		data.setLong(UAMSApiField.END_TIME, endTime);

		return data;
	}

	private void sendMail(String content) throws AddressException, MessagingException {
		System.out.println("send email .....");
		// setup Mail Server Properties ...
		Properties mailServer = new Properties();
		mailServer.put("mail.smtp.port", "587");
		mailServer.put("mail.smtp.auth", "true");
		mailServer.put("mail.smtp.host", "smtp.gmail.com");
		mailServer.put("mail.smtp.starttls.enable", "true");

		// get mail sessions
		Session session = Session.getDefaultInstance(mailServer, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("cntt071093@gmail.com", "muaxuantinhyeu");
			}
		});
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress("cntt071093@gmail.com"));
		message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(EMAL_1));
		message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(EMAIL_2));
		message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(EMAIL_3));
		message.setSubject("Nho Admin daily report");
		message.setContent(content, "text/html");

		// send mail
		Transport transport = session.getTransport("smtp");
		transport.connect("smtp.gmail.com", "cntt071093@gmail.com", "muaxuantinhyeu");
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

	}
}
