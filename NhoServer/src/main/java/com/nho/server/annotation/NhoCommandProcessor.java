package com.nho.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nho.message.MessageType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NhoCommandProcessor {
	MessageType[] command();
}
