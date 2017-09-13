package com.nho.server.exception;

public class IdHandlerException extends AbstractHandlerException {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		getLogger().debug(":(( receive new id exception");
	}
}
