package com.nho.server.processors;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nho.message.MessageType;
import com.nho.message.request.Request;
import com.nho.server.NhoServer;
import com.nho.server.processors.impl.AbstractNhoProcessor;

public class NhoCommandRouter {

	private final Map<MessageType, NhoRequestProcessor> commands;
	private final NhoServer context;

	public NhoCommandRouter(NhoServer context) {
		this.context = context;
		this.commands = new ConcurrentHashMap<>();
	}
	
	public void registerCommand(MessageType command, NhoRequestProcessor processor) {
		this.commands.put(command, processor);
		if (processor instanceof AbstractNhoProcessor<?>) {
			((AbstractNhoProcessor<?>) processor).setContext(this.context);
		}
	}

	public void deregisterCommand(MessageType command) {
		this.commands.remove(command);
	}

	public void init(Map<MessageType, Class<?>> commandRouting) throws Exception{
		for(Entry<MessageType, Class<?>> entry : commandRouting.entrySet()){
			NhoRequestProcessor processor = (NhoRequestProcessor) entry.getValue().newInstance();
			if(processor instanceof AbstractNhoProcessor<?>){
				((AbstractNhoProcessor<?>)processor).setContext(context);
			}
			this.commands.put(entry.getKey(), processor);
		}
		System.out.println("Load number nho processor is "+this.commands.size());
	}
	
	
	public void process(Request message) {
		if (message == null) {
			return;
		}
		NhoRequestProcessor processor = this.commands.get(message.getType());
		if (processor == null) {
			throw new RuntimeException("Processor not found for command " + message.getType());
		}
		processor.execute((Request) message);
	}
}
