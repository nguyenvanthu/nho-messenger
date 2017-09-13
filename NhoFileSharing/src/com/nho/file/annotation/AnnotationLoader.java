package com.nho.file.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class AnnotationLoader {
	public static Map<String, Class<?>> load(String packageName) {
		Map<String, Class<?>> commandRouting = new HashMap<>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(FileCommandProcessor.class);
		for (Class<?> clazz : classes) {
			FileCommandProcessor commands = clazz.getAnnotation(FileCommandProcessor.class);
			String[] values = commands.command();
			for (String value : values) {
				commandRouting.put(value, clazz);
			}
		}
		return commandRouting;
	}
}
