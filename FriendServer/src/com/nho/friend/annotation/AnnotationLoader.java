package com.nho.friend.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.nho.friend.statics.FriendCommand;

public class AnnotationLoader {

	public static Map<FriendCommand, Class<?>> load(String packageName) {
		Map<FriendCommand, Class<?>> commandRouting = new HashMap<>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(FriendCommandProcessor.class);
		for (Class<?> clazz : classes) {
			FriendCommandProcessor commands = clazz.getAnnotation(FriendCommandProcessor.class);
			FriendCommand[] values = commands.command();
			for (FriendCommand value : values) {
				commandRouting.put(value, clazz);
			}
		}
		return commandRouting;
	}
}
