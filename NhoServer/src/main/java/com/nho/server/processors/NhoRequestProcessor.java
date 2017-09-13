package com.nho.server.processors;

import com.nho.message.request.Request;

public interface NhoRequestProcessor {

	void execute(Request request);
}
