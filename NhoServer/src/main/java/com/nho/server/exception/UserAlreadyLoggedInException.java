package com.nho.server.exception;

public class UserAlreadyLoggedInException extends NhoException {

	private static final long serialVersionUID = 1L;

	private final String userName;
	private final String sessionId;

	public UserAlreadyLoggedInException(String userName, String sessionId) {
		this.userName = userName;
		this.sessionId = sessionId;
	}

	public UserAlreadyLoggedInException(String userName, String sessionId, String message) {
		super(message);
		this.userName = userName;
		this.sessionId = sessionId;
	}

	public UserAlreadyLoggedInException(String userName, String sessionId, String message, Throwable cause) {
		super(message, cause);
		this.userName = userName;
		this.sessionId = sessionId;
	}

	public String getUserName() {
		return userName;
	}

	public String getSessionId() {
		return sessionId;
	}
}
