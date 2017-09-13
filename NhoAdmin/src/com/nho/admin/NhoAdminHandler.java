package com.nho.admin;

import com.mario.entity.impl.BaseCommandRoutingHandler;
import com.mario.entity.message.Message;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.messaging.rabbit.producer.RabbitMQRPCProducer;
import com.nho.admin.annotation.AnnotationLoader;
import com.nho.admin.reporter.AdminCommander;
import com.nho.admin.router.AdminCommandRouter;
import com.nho.uams.statics.UAMSApiField;

public class NhoAdminHandler extends BaseCommandRoutingHandler {
	

	private AdminCommandRouter router;
	private String applicationId;
	private RabbitMQRPCProducer uams_producer;

	public void init(PuObjectRO initParams) {
		this.applicationId = initParams.getString(UAMSApiField.APPLICATION_ID);
		this.uams_producer = getApi().getProducer("admin_uams_rabbitmq_producer");
		this.router = new AdminCommandRouter(this);
		try {
			router.init(AnnotationLoader.load("com.nho.admin.router"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		reportDaily();
	}
	
	private void reportDaily(){
		getLogger().debug("start report daily");
		getLogger().debug(Thread.currentThread().getName());
		AdminCommander commander = new AdminCommander(this);
		commander.report();
	}
	
	@Override
	public PuElement handle(Message message) {
		PuObject data = (PuObject) message.getData();
		getLogger().debug(data.toJSON());
		data.setType("command", PuDataType.STRING);
		if (data.variableExists("command")) {
			String command = data.getString("command");
			getLogger().debug("name of command " + command);
			try {
				return this.router.process(command, data);
			} catch (Exception e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>("status", 1));
	}
	
	public RabbitMQRPCProducer getUAMSProducer(){
		return this.uams_producer;
	}
	
	public String getApplicationId(){
		return this.applicationId;
	}
}
