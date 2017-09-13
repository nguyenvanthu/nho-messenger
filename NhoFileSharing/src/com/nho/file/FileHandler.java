package com.nho.file;

import com.hazelcast.core.HazelcastInstance;
import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.mongodb.MongoClient;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.db.models.ModelFactory;
import com.nho.file.annotation.AnnotationLoader;
import com.nho.file.exception.FileException;
import com.nho.file.router.FileCommandRouter;
import com.nho.file.statics.FileField;

public class FileHandler extends BaseMessageHandler {
	private FileCommandRouter commandRouter;
	private HazelcastInstance hazelcast;
	private ModelFactory modelFactory;
	private MongoClient mongoClient;

	@Override
	public void init(PuObjectRO initParams) {
		super.init(initParams);
		getLogger().debug("start file sharing server ...");
		ModelFactory modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		mongoClient = getApi().getMongoClient(initParams.getString(FileField.MONGODB));
		this.hazelcast = getApi().getHazelcastInstance(initParams.getString(FileField.HAZELCAST));
		modelFactory.setMongoClient(mongoClient);
		modelFactory.setHazelcast(hazelcast);
		this.modelFactory = modelFactory;
		initDatabase();
		this.commandRouter = new FileCommandRouter(this);
		try {
			commandRouter.init(AnnotationLoader.load("com.nho.file.router"));
		} catch (Exception e) {
			getLogger().debug("error when register command processor in File Sharing Server");
			throw new RuntimeException(e);
		}
	}

	@Override
	public PuElement handle(Message message) {
		PuObject data = (PuObject) message.getData();
		if(data == null){
			PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
		}
		if (data.variableExists(FileField.COMMAND)) {
			String command = data.getString(FileField.COMMAND);
			getLogger().debug("handle command: " + command);
			try {
				return this.commandRouter.process(command, data);
			} catch (FileException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		PuObject data = (PuObject) requestParams;
		if (data.variableExists(FileField.COMMAND)) {
			String command = data.getString(FileField.COMMAND);
			getLogger().debug("internal process command: " + command);
			try {
				return this.commandRouter.process(command, data);
			} catch (FileException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public HazelcastInstance getHazelcast() {
		return this.hazelcast;
	}

	private void initDatabase() {

	}
}
