package com.nho.monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.nhb.common.BaseLoggable;
import com.nhb.common.vo.HostAndPort;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;
import com.nhb.eventdriven.impl.BaseEventHandler;
import com.nho.client.NhoClient;
import com.nho.client.NhoEvent;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.PingRequest;
import com.nho.message.response.connection.ConnectionResponse;

public class NhoMonitoring extends BaseLoggable {
	public static final long TIME_OUT = 2 * 60 * 1000L;
	private static final String EMAIL = "thunv@phugroup.com";
	private static final String EMAL_1 = "admin@phugroup.com";
	private static final String EMAIL_2 = "mr.vietanh@gmail.com";
	private long lastPongTime;
	private NhoClient nhoClient;
	private ScheduledExecutorService timer = Executors.newScheduledThreadPool(2);

	// Bootstrap main method
	public static void main(String[] args) throws InterruptedException {
		// Initializer.bootstrap(NhoMonitoring.class);

		NhoMonitoring app = new NhoMonitoring(args);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			{
				this.setPriority(MAX_PRIORITY);
				this.setName("Shutdown thread");
			}

			@Override
			public void run() {
				app.stop();
			}
		});
		app.start();
	}

	private EventHandler onConnectionResponse = new BaseEventHandler(this, "onConnectionResponse");
	private EventHandler onDisconnect = new BaseEventHandler(this, "onDisconnect");
	private EventHandler onPong = new BaseEventHandler(this, "onPong");

	private NhoMonitoring(String[] args) {

		this.nhoClient = new NhoClient(new HostAndPort(System.getProperty("server.host", "52.20.97.213"),
				Integer.valueOf(System.getProperty("server.port", "9999"))), true);

		System.out.println("connecting to: " + this.nhoClient.getServerAddress());

		nhoClient.addEventListener(MessageType.CONNECTION_RESPONSE, this.onConnectionResponse);
		nhoClient.addEventListener(MessageType.DISCONNECT_EVENT, this.onDisconnect);
		nhoClient.addEventListener(MessageType.PONG, this.onPong);

	}

	private void start() throws InterruptedException {
		try {
			nhoClient.connect();
			this.lastPongTime = 0;
			checkTimeoutTask();
		} catch (IOException e) {
			// connect fail
			getLogger().error("Unable to connect", e);
			System.out.println("cannot start nho monitoring , restart after 3 minutes ...");
			Thread.sleep(3 * 60 * 1000);
			restart();
		}
	}

	private void stop() {
		nhoClient.close();
		timer.shutdown();
	}

	private void monitorLag() {
		timer.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				ping();
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	private void checkTimeoutTask() {
		timer.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				checkTimeOut();
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	private void checkTimeOut() {
		long currentTime = System.currentTimeMillis();
		if (this.lastPongTime == 0) {
			return;
		}
		if (currentTime - this.lastPongTime > TIME_OUT) {
			System.out.println("request timeout , restart after 3 minutes");
			try {
			
				sendMail("cannot connect to server at "+getCurrentTime());
				Thread.sleep(3 * 60 * 1000);
				restart();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	private long timePing ;
	
	private void ping() {
		System.out.println("send ping request");
		PingRequest ping = new PingRequest();
		timePing = System.currentTimeMillis();
		this.nhoClient.send(ping);
	}

	private <T extends NhoMessage> T getMessage(Event event) {
		NhoEvent nhoEvent = (NhoEvent) event;
		return nhoEvent.getMessage();
	}

	@Deprecated
	public void onConnectionResponse(Event event) {
		ConnectionResponse response = getMessage(event);
		getLogger().debug("session of user is " + response.getSessionId());
		getLogger().debug("Connect " + (response.isSuccessful() ? "success" : "failure"));
		if (response.isSuccessful()) {
			System.out.println("connect sucessful");
			this.monitorLag();
		} else {
			getLogger().debug("cannot connect to server ,try again after 3 minutes");
			try {
				System.out.println("cannot connect to server , restart after 3 minutes");
				sendMail("cannot connect to server at "+getCurrentTime());
				Thread.sleep(3*60*1000);
				restart();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

		}
	}

	@Deprecated
	public void onPong(Event event) {
		System.out.println("receive pong message");
		long currentTime = System.currentTimeMillis();
		System.out.println("round trip:  "+(currentTime - this.timePing));
		this.lastPongTime = currentTime;
	}

	@Deprecated
	public void onDisconnect(Event event) {
		getLogger().debug("Disconnected, server die ");
		try {
			if (!isLostNetwork()) {
				System.out.println("server disconnect , restart after 3 minutes");
				// server disconnect
				sendMail("server disconnected at "+getCurrentTime());
				Thread.sleep(3*60*1000);
				restart();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void restart() throws InterruptedException {
		stop();
		start();
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

	private boolean isLostNetwork() throws IOException {
		boolean isLost = false;
		int timeout = 3000;
		InetAddress[] addresses = InetAddress.getAllByName("www.google.com");
		for (InetAddress address : addresses) {
			if (!address.isReachable(timeout)) {
				isLost = true;
			}
		}
		return isLost;
	}
	
	private String getCurrentTime(){
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return format.format(date)+" - "+ TimeZone.getTimeZone("GMT+07:00").getDisplayName();
	}
}


