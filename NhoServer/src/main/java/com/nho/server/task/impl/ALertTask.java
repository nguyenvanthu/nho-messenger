package com.nho.server.task.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.nho.server.NhoServer;
import com.nho.server.statics.Counter;
import com.nho.server.task.AbstractTask;

public class ALertTask extends AbstractTask {
	private static final String EMAIL = "thunv@phugroup.com";
	private static final String EMAL_1 = "admin@phugroup.com";
	private static final String EMAIL_2 = "mr.vietanh@gmail.com";

	public ALertTask(NhoServer context) {
		super.setContext(context);
	}

	@Override
	public void run() {
		if (Counter.getInteraction() < 0.8 * Counter.getThreshold()) {
			// alert to administration
			try {
				getLogger().debug("send mail alert ");
				String alert = "Server has " + Counter.getInteraction() + " fewer than the average "
						+ Counter.getThreshold() + " interactions";
				sendMail(alert);
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		Counter.updateThreshold();
		Counter.resetInteraction();
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
				return new PasswordAuthentication("nguyenvanthu20112265@gmail.com", "thao071093");
			}
		});
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress("nguyenvanthu20112265@gmail.com"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL));
		message.addRecipient(Message.RecipientType.CC, new InternetAddress(EMAL_1));
		message.addRecipient(Message.RecipientType.CC, new InternetAddress(EMAIL_2));
		message.setSubject("ALert Nho Server status");
		message.setContent(content, "text/html");

		// send mail
		Transport transport = session.getTransport("smtp");
		transport.connect("smtp.gmail.com", "nguyenvanthu20112265@gmail.com", "thao071093");
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

	}
}
