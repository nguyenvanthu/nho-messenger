package com.nho.admin.helper;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.admin.NhoAdminHandler;

public class LoggingHelper extends NhoAdminAbstractHelper{
	public LoggingHelper(NhoAdminHandler context){
		super.setContext(context);
	}
	
	public PuObject getLogFromUAMS(PuObject data){
		getLogger().debug("send request to uams by rabbitmq");
		RPCFuture<PuElement> publish = this.getContext().getUAMSProducer().publish(data);
		try{
			PuObject result = (PuObject) publish.get();
			return result;
		}catch(Exception exception){
			getLogger().debug("error when get data from rabbit mq");
		}
		return null;	
		
	}
}
