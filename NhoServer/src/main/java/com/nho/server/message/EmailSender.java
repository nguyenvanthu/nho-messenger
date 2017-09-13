package com.nho.server.message;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	private static final String EMAIL = "nho.messenger@gmail.com";

	public void process(String email, String password, String title, String message)
			throws Exception, MessagingException {
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
				return new PasswordAuthentication(email, password);
			}
		});
		MimeMessage emailMessage = new MimeMessage(session);
		emailMessage.setFrom(new InternetAddress(email));
		emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL));
		emailMessage.setSubject(title);
		emailMessage.setContent(message, "text/html");

		// send mail
		Transport transport = session.getTransport("smtp");
		transport.connect("smtp.gmail.com", email, password);
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
	}
}
