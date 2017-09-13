package com.nho.uams.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuElement;
import com.nho.uams.UAMSHandler;
import com.nho.uams.exception.UAMSException;
import com.nho.uams.message.UAMSMessage;
import com.nho.uams.message.UAMSMessageType;

public class UAMSCommandRouter {
	private final Map<UAMSMessageType, UAMSProcessor> processors;
	private final UAMSHandler context;

	public UAMSCommandRouter(UAMSHandler context) {
		this.context = context;
		this.processors = new ConcurrentHashMap<>();
	}

	public void init(Map<UAMSMessageType, Class<?>> commandRouting) throws Exception {
		for (Entry<UAMSMessageType, Class<?>> entry : commandRouting.entrySet()) {
			UAMSProcessor processor = (UAMSProcessor) entry.getValue().newInstance();
			if (processor instanceof UAMSAbstractProcessor<?>) {
				((UAMSAbstractProcessor<?>) processor).setContext(context);
			}
			this.processors.put(entry.getKey(), processor);
		}
		System.out.println("Number uams processors is " + this.processors.size());
	}

	public PuElement process( UAMSMessage request) throws UAMSException {
		
		UAMSProcessor processor = this.processors.get(request.getType());
		if (processor == null) {
			throw new UAMSException("Processor not found for command " + request.getType());
		}
		return processor.execute(request);
	}
}
