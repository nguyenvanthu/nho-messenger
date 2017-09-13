package com.nho.server.exception;

import com.nho.server.NhoServer;

public class FriendException extends AbstractException{
	public FriendException(String message, Throwable ex,NhoServer context) {
		super(message, ex);
	}

	private static final long serialVersionUID = 1L;

}
