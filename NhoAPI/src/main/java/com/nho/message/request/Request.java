package com.nho.message.request;

import com.nho.message.MessageType;

public interface Request {

	MessageType getType();

	String getSessionId();
}
