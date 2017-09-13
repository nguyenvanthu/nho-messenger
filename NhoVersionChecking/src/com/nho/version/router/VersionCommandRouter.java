package com.nho.version.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.version.VersionCheckingHandler;

public class VersionCommandRouter {
	private Map<String, VersionProcessor> processors ;
	private VersionCheckingHandler context ;
	
	public VersionCommandRouter(VersionCheckingHandler context) {
		this.context = context;
		this.processors = new ConcurrentHashMap<>();
	}
	
	public void init(Map<String, Class<?>> commandRouting) throws Exception{
		for (Entry<String, Class<?>> entry : commandRouting.entrySet()) {
			VersionProcessor processor = (VersionProcessor) entry.getValue().newInstance();
			if(processor instanceof VersionAbstractProcessor){
				((VersionAbstractProcessor) processor).setContext(context);
			}
			this.processors.put(entry.getKey(), processor);
		}
	}
	
	public PuElement process(String command,PuObjectRO request) throws Exception{
		if (command == null) {
			throw new Exception("command cannot be null");
		}
		VersionProcessor processor = this.processors.get(command);
		if (processor == null) {
			throw new Exception("Processor not found for command " + command);
		}
		return processor.execute(request);
	}
	
}
