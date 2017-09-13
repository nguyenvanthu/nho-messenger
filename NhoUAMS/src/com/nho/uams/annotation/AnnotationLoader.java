package com.nho.uams.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.nho.uams.message.UAMSMessageType;

public class AnnotationLoader {
	public static Map<UAMSMessageType, Class<?>> load(String packageName) {
		Map<UAMSMessageType, Class<?>> commandRouting = new HashMap<>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(UAMSCommandProcessor.class);
		for (Class<?> clazz : classes) {
			UAMSCommandProcessor commands = clazz.getAnnotation(UAMSCommandProcessor.class);
			UAMSMessageType[] values = commands.command();
			for (UAMSMessageType value : values) {
				commandRouting.put(value, clazz);
			}
		}
		return commandRouting;
	}
}
