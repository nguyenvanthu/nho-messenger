package com.nho.notification.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.nho.notification.statics.NotifcationCommand;

public class AnnotationLoader {

	public static Map<NotifcationCommand, Class<?>> load(String packageName) {
		Map<NotifcationCommand, Class<?>> commandRouting = new HashMap<>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(NotificationCommandProcessor.class);
		for (Class<?> clazz : classes) {
			NotificationCommandProcessor commands = clazz.getAnnotation(NotificationCommandProcessor.class);
			NotifcationCommand[] values = commands.command();
			for (NotifcationCommand value : values) {
				commandRouting.put(value, clazz);
			}
		}
		return commandRouting;
	}
}
