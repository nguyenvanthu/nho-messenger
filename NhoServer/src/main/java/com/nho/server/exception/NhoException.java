package com.nho.server.exception;

public class NhoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NhoException() {
		super();
	}

	public NhoException(String message) {
		super(message);
	}

	public NhoException(String message, Throwable ex) {
		super(message, ex);
	}

	public NhoException(String message, Throwable ex, boolean enableSuppression, boolean writableStackTrace) {
		super(message, ex, enableSuppression, writableStackTrace);
	}
}
