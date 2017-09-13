package com.nho.chat.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.ChatHandler;
import com.nho.chat.exception.ChatException;
import com.nho.chat.statics.ChannelCommand;

public class ChatCommandRouter {
	private final Map<ChannelCommand, ChatProcessor> processors;
	private final ChatHandler context ;
	
	public ChatCommandRouter(ChatHandler context) {
		this.context = context;
		this.processors = new ConcurrentHashMap<>();
	}
	
	public void init(Map<ChannelCommand, Class<?>> commandRouting) throws Exception{
		for (Entry<ChannelCommand, Class<?>> entry : commandRouting.entrySet()) {
			ChatProcessor processor = (ChatProcessor) entry.getValue().newInstance();
			if(processor instanceof ChatAbstractProcessor){
				((ChatAbstractProcessor) processor).setContext(context);
			}
			this.processors.put(entry.getKey(), processor);
		}
		System.out.println("Number chat processors is "+this.processors.size());
	}
	
	public PuElement process(ChannelCommand command,PuObjectRO request) throws ChatException{
		if (command == null) {
			throw new ChatException("command cannot be null");
		}
		ChatProcessor processor = this.processors.get(command);
		if (processor == null) {
			throw new ChatException("Processor not found for command " + command);
		}
		return processor.execute(request);
	}
}
