package com.nho.server.processors.feedback;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.nho.message.MessageType;
import com.nho.message.request.feedback.SendFeedback;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
@NhoCommandProcessor(command={MessageType.FEEDBACK_MESSAGE})
public class SendFeedbackProcessor extends AbstractNhoProcessor<SendFeedback> {

	@Override
	protected void process(SendFeedback request) throws Exception {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null) {
			getLogger().debug("user null");
			return;
		}
		getLogger().debug("send feedback of user {}", user.getUserName());
		this.send(request.getTitle(), request.getMessage());
	}

	private static final String EMAIL = "nho.messenger@gmail.com";
	private static final String SENDER = "nguyenvanthu20112265@gmail.com";
	private static final String PASSWORD = "thao071093";

	private void send(String title, String message) throws Exception, MessagingException {
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
				return new PasswordAuthentication(SENDER, PASSWORD);
			}
		});
		MimeMessage emailMessage = new MimeMessage(session);
		emailMessage.setFrom(new InternetAddress(SENDER));
		emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL));
		emailMessage.setSubject(title);
		emailMessage.setContent(message, "text/html");

		// send mail
		Transport transport = session.getTransport("smtp");
		transport.connect("smtp.gmail.com", SENDER, PASSWORD);
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
	}
}
