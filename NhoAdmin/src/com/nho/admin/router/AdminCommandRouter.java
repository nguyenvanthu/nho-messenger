package com.nho.admin.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.admin.NhoAdminHandler;

public class AdminCommandRouter {
	private Map<String, AdminProcessor> processors ;
	private NhoAdminHandler context ;
	
	public AdminCommandRouter(NhoAdminHandler context) {
		this.context = context;
		this.processors = new ConcurrentHashMap<>();
	}
	
	public void init(Map<String, Class<?>> commandRouting) throws Exception{
		for (Entry<String, Class<?>> entry : commandRouting.entrySet()) {
			AdminProcessor processor = (AdminProcessor) entry.getValue().newInstance();
			if(processor instanceof AdminAbstractProcessor){
				((AdminAbstractProcessor) processor).setContext(context);
			}
			this.processors.put(entry.getKey(), processor);
		}
	}
	
	public PuElement process(String command,PuObjectRO request) throws Exception{
		if (command == null) {
			throw new Exception("command cannot be null");
		}
		AdminProcessor processor = this.processors.get(command);
		if (processor == null) {
			throw new Exception("Processor not found for command " + command);
		}
		return processor.execute(request);
	}
}
