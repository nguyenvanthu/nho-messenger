package com.nho.server.exception;

import com.nho.server.NhoServer;

public class ChatException extends AbstractException{
	public ChatException(String message, Throwable ex,NhoServer context) {
		super(message, ex);
	}

	private static final long serialVersionUID = 1L;
	
}
