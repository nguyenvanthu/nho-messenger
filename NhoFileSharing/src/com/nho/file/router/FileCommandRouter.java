package com.nho.file.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.file.FileHandler;
import com.nho.file.exception.FileException;

public class FileCommandRouter {
	private final Map<String, FileProcessor> processors;
	private final FileHandler context;

	public FileCommandRouter(FileHandler context) {
		this.context = context;
		this.processors = new ConcurrentHashMap<>();
	}

	public void init(Map<String, Class<?>> commandRouting) throws Exception {
		for (Entry<String, Class<?>> entry : commandRouting.entrySet()) {
			FileProcessor processor = (FileProcessor) entry.getValue().newInstance();
			if (processor instanceof FileAbstractProcessor) {
				((FileAbstractProcessor) processor).setContext(context);
			}
			this.processors.put(entry.getKey(), processor);
		}
		System.out.println("Number chat processors is " + this.processors.size());
	}

	public PuElement process(String command, PuObjectRO request) throws FileException {
		if (command == null) {
			throw new FileException("command cannot be null");
		}
		FileProcessor processor = this.processors.get(command);
		if (processor == null) {
			throw new FileException("Processor not found for command " + command);
		}
		return processor.execute(request);
	}
}
