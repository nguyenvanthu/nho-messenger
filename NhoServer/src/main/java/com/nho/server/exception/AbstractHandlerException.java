package com.nho.server.exception;

import com.nhb.common.BaseLoggable;

public abstract class AbstractHandlerException extends BaseLoggable implements Thread.UncaughtExceptionHandler {
//	public void sendExceptionLog(ExceptionType type, NhoServer context, String message, String strace) {
//		getLogger().debug("send exception to NhoTracking ");
//		getLogger().debug("title " + message);
//		getLogger().debug("stackTrace " + strace);
//		PuObject data = new PuObject();
//		data.setString("command", "sendExceptionLog");
//		data.setString(EF.TITLE, message);
//		data.setString(EF.STACKTRACE, strace);
//		data.setInteger(EF.TYPE, type.getCode());
//		context.getApi().call(HandlerCollection.NHO_TRACKING, data);
//	}
}
