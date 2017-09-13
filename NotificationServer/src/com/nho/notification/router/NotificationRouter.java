package com.nho.notification.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.notification.NotificationHandler;
import com.nho.notification.exception.NotificationException;
import com.nho.notification.statics.NotifcationCommand;

public class NotificationRouter {
	private Map<NotifcationCommand, NotificationProcessor> processors ;
	private NotificationHandler context ;
	
	public NotificationRouter(NotificationHandler context) {
		this.context = context;
		this.processors = new ConcurrentHashMap<>();
	}
	
	public void init(Map<NotifcationCommand, Class<?>> commandRouting) throws Exception{
		for (Entry<NotifcationCommand, Class<?>> entry : commandRouting.entrySet()) {
			NotificationProcessor processor = (NotificationProcessor) entry.getValue().newInstance();
			if(processor instanceof NotificationAbstractProcessor){
				((NotificationAbstractProcessor) processor).setContext(context);
			}
			this.processors.put(entry.getKey(), processor);
		}
	}
	
	public PuElement process(NotifcationCommand command,PuObjectRO request) throws NotificationException{
		if (command == null) {
			throw new NotificationException("command cannot be null");
		}
		NotificationProcessor processor = this.processors.get(command);
		if (processor == null) {
			throw new NotificationException("Processor not found for command " + command);
		}
		return processor.execute(request);
	}
	
}
