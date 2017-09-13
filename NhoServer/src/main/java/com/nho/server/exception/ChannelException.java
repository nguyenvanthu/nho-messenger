package com.nho.server.exception;

import com.nho.server.NhoServer;

public class ChannelException extends AbstractException{

	public ChannelException(String message, Throwable ex,NhoServer context) {
		super(message, ex);
		System.out.println("have new Channel exception");
	}

	private static final long serialVersionUID = 1L;

}
