package com.nho.chat.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import com.nho.chat.statics.ChannelCommand;

public class AnnotationLoader {

	public static Map<ChannelCommand, Class<?>> load(String packageName) {
		Map<ChannelCommand, Class<?>> commandRouting = new HashMap<>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ChatCommandProcessor.class);
		for (Class<?> clazz : classes) {
			ChatCommandProcessor commands = clazz.getAnnotation(ChatCommandProcessor.class);
			ChannelCommand[] values = commands.command();
			for (ChannelCommand value : values) {
				commandRouting.put(value, clazz);
			}
		}
		return commandRouting;
	}
}
