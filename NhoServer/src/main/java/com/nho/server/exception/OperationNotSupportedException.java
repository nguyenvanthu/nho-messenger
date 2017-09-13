package com.nho.server.exception;

public class OperationNotSupportedException extends NhoException {

	private static final long serialVersionUID = 1L;

	public OperationNotSupportedException() {

	}

	public OperationNotSupportedException(String message) {
		super(message);
	}

	public OperationNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}
}
