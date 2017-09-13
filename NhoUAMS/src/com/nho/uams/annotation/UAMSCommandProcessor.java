package com.nho.uams.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nho.uams.message.UAMSMessageType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UAMSCommandProcessor {
	UAMSMessageType[] command();
}
