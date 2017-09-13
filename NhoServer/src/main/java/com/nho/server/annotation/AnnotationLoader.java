package com.nho.server.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.nhb.common.BaseLoggable;
import com.nho.message.MessageType;

public class AnnotationLoader extends BaseLoggable {
	public static Map<MessageType, Class<?>> load(String packageName){
		
		Map<MessageType, Class<?>> commandRouting = new HashMap<>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(NhoCommandProcessor.class);
		for(Class<?> clazz : classes ){
			NhoCommandProcessor commands = clazz.getAnnotation(NhoCommandProcessor.class);
			MessageType[] values = commands.command();
			for(MessageType value : values ){
				commandRouting.put(value, clazz);
			}
		}
		
		return commandRouting;
	}
}
