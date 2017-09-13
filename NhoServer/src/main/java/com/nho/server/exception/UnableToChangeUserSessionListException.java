package com.nho.server.exception;

public class UnableToChangeUserSessionListException extends NhoException {

	private static final long serialVersionUID = 1L;

	public UnableToChangeUserSessionListException() {

	}

	public UnableToChangeUserSessionListException(String message) {
		super(message);
	}

	public UnableToChangeUserSessionListException(String message, Throwable ex) {
		super(message, ex);
	}
}
