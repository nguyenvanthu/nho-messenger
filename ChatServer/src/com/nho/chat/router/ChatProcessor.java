package com.nho.chat.router;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.exception.ChatException;

public interface ChatProcessor {
	PuElement execute(PuObjectRO request) throws ChatException;
}
