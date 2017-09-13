package com.nho.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.common.Loggable;
import com.nhb.common.annotations.Transparent;

public abstract class AbstractException extends RuntimeException implements Loggable{
	private static final long serialVersionUID = 1L;
	private String message;
	private Throwable ex;
	private Logger logger = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getEx() {
		return ex;
	}

	public void setEx(Throwable ex) {
		this.ex = ex;
	}

	public AbstractException(String message, Throwable ex) {
		super(message, ex);
		this.message = message;
		this.ex = ex;
	}

//	public void sendExceptionLog(ExceptionType type, NhoServer context) {
//		getLogger().debug("send exception to NhoTracking ");
//		getLogger().debug("title " + this.message);
//		getLogger().debug("stackTrace " + this.ex.getCause().toString());
//		PuObject data = new PuObject();
//		data.setString("command", "sendExceptionLog");
//		data.setString(EF.TITLE, this.message);
//		data.setString(EF.STACKTRACE, this.ex.getCause().toString());
//		data.setInteger(EF.TYPE, type.getCode());
//		context.getApi().call(HandlerCollection.NHO_TRACKING, data);
//	}
	@Override
	@Transparent
	public Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(getClass());
		}
		return logger;
	}

	@Override
	@Transparent
	public Logger getLogger(String name) {
		return LoggerFactory.getLogger(name);
	}
}
