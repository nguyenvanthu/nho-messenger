package com.nho.friend.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.friend.FriendHandler;
import com.nho.friend.exception.FriendException;
import com.nho.friend.statics.FriendCommand;


public class FriendCommandRouter {
	private final Map<FriendCommand, FriendProcessor> processors;
	private final FriendHandler context;

	public FriendCommandRouter(FriendHandler context) {
		this.context = context;
		this.processors = new ConcurrentHashMap<>();
	}
	public void init(Map<FriendCommand, Class<?>> commandRouting) throws Exception{
		for (Entry<FriendCommand, Class<?>> entry : commandRouting.entrySet()) {
			FriendProcessor processor = (FriendProcessor) entry.getValue().newInstance();
			if(processor instanceof FriendAbstractProcessor){
				((FriendAbstractProcessor) processor).setContext(context);
			}
			this.processors.put(entry.getKey(), processor);
		}
		System.out.println("Number friend processors is "+this.processors.size());
	}
	
	public PuElement process(FriendCommand command,PuObjectRO request) throws FriendException{
		if (command == null) {
			throw new FriendException("command cannot be null");
		}
		FriendProcessor processor = this.processors.get(command);
		if (processor == null) {
			throw new FriendException("Processor not found for command " + command);
		}
		return processor.execute(request);
	}
}
