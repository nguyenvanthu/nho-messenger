package com.nho.uams;

import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.db.cassandra.daos.CassandraDAOFactory;
import com.nhb.common.db.models.ModelFactory;
import com.nho.uams.annotation.AnnotationLoader;
import com.nho.uams.exception.UAMSException;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.router.UAMSCommandRouter;
import com.nho.uams.statics.UAMSField;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

public class UAMSHandler extends BaseMessageHandler {
	private ModelFactory modelFactory;
	private UAMSCommandRouter commandRouter;

	@Override
	public void init(PuObjectRO initParams) {
		modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		modelFactory.setCassandraDAOFactory(
				new CassandraDAOFactory(getApi().getCassandraDataSource(initParams.getString(UAMSField.CASSANDRA))));
		this.commandRouter = new UAMSCommandRouter(this);
		try {
			commandRouter.init(AnnotationLoader.load("com.nho.uams.router"));
		} catch (Exception e) {
			getLogger().debug("error when register command processor in Nho UAMS Server");
			throw new RuntimeException(e);
		}
		// createKafKaTopic(initParams.getString(UAMSField.TOPIC));
		getLogger().debug("Nho UAMS starting ....");
	}

	@SuppressWarnings("unused")
	private void createKafKaTopic(String topic) {
		ZkClient zkClient = null;
		ZkUtils zkUtils = null;
		try {
			String zookeeperHosts = "localhost:2181";
			int sessionTimeOut = 15 * 1000;
			int connectionTimeOut = 10 * 1000;
			zkClient = new ZkClient(zookeeperHosts, sessionTimeOut, connectionTimeOut, ZKStringSerializer$.MODULE$);
			zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);

			int noOfPartitions = 2;
			int noOfReplication = 1;
			Properties topicConfiguration = new Properties();
			if (!AdminUtils.topicExists(zkUtils, topic)) {
				AdminUtils.createTopic(zkUtils, topic, noOfPartitions, noOfReplication, topicConfiguration, null);
				getLogger().debug("create topic in kafka success");
			} else {
				getLogger().debug("topic is already created");
			}
		} catch (Exception exception) {
			getLogger().debug("error when create kafka topic", exception);
		}
	}

	@Override
	public PuElement handle(Message message) {
		getLogger().debug("receive log message in handle function");
//		PuElement data = (PuObject) message.getData();
//		PuArray arr = (PuArray) data;
//		UAMSAbstractMessage request = UAMSAbstractMessage.deserialize(arr);
		PuObject data = (PuObject) message.getData();
		UAMSAbstractMessage request = MessageConverter.convertFromPuObject(data);
		try {
			return this.commandRouter.process(request);
		} catch (UAMSException e) {
			getLogger().debug("process command exception", e);
		}

		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		getLogger().debug("receive log message in interop function");
		PuObject data = (PuObject) requestParams;
		UAMSAbstractMessage request = MessageConverter.convertFromPuObject(data);
		try {
			return this.commandRouter.process(request);
		} catch (UAMSException e) {
			getLogger().debug("process command exception", e);
		}
		return PuObject.fromObject(new MapTuple<>(UAMSField.STATUS, 1));
	}

	public ModelFactory getModelFactory() {
		return this.modelFactory;
	}

	
}