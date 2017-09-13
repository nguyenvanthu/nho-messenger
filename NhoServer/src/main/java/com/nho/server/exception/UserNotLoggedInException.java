package com.nho.server.exception;

public class UserNotLoggedInException extends NhoException {

	private static final long serialVersionUID = 1L;

	public UserNotLoggedInException() {

	}

	public UserNotLoggedInException(String message) {
		super(message);
	}
}
