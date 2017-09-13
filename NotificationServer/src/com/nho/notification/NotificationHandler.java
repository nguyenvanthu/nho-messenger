package com.nho.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.hazelcast.core.HazelcastInstance;
import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.db.models.ModelFactory;
import com.nho.notification.annotation.AnnotationLoader;
import com.nho.notification.entity.PushNotificationManager;
import com.nho.notification.exception.NotificationException;
import com.nho.notification.router.NotificationRouter;
import com.nho.notification.statics.DeviceTokenDbField;
import com.nho.notification.statics.NotifcationCommand;
import com.nho.statics.F;

public class NotificationHandler extends BaseMessageHandler {
	private NotificationRouter commandRouter;
	private ModelFactory modelFactory;
	private PushNotificationManager pushNotificationManager;
	private String hermesHost;
	private boolean modeTest;
	private HazelcastInstance hazelcast;
	private MongoClient mongoClient;

	@Override
	public void init(PuObjectRO initParams) {
		super.init(initParams);
		getLogger().debug("statrting ChatServer ....");
		ModelFactory modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		mongoClient = getApi().getMongoClient(initParams.getString(F.MONGODB));
		modelFactory.setMongoClient(mongoClient);
		this.modelFactory = modelFactory;
		initDatabase();
		this.pushNotificationManager = new PushNotificationManager(this);
		this.hermesHost = initParams.getString(F.HERMES_HOST);
		this.hazelcast = getApi().getHazelcastInstance(initParams.getString(F.HAZELCAST));
		this.modeTest = initParams.getBoolean("modeTest");
		this.commandRouter = new NotificationRouter(this);
		try {
			commandRouter.init(AnnotationLoader.load("com.nho.notification.router"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PuElement handle(Message message) {
		PuObject data = (PuObject) message.getData();
		if (data.variableExists(F.COMMAND)) {
			NotifcationCommand command = NotifcationCommand.fromCode(data.getInteger(F.COMMAND));
			try {
				return this.commandRouter.process(command, data);
			} catch (NotificationException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		PuObject data = (PuObject) requestParams;
		if (data.variableExists(F.COMMAND)) {
			NotifcationCommand command = NotifcationCommand.fromCode(data.getInteger(F.COMMAND));
			try {
				return this.commandRouter.process(command, data);
			} catch (NotificationException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public PushNotificationManager getPushNotificationManager() {
		return this.pushNotificationManager;
	}

	public String getHermesHost() {
		return this.hermesHost;
	}

	public boolean getModeTest() {
		return this.modeTest;
	}

	public HazelcastInstance getHazelcast() {
		return this.hazelcast;
	}

	private void initDatabase() {
		createDatabaseIndexes(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION,
				new ArrayList<>(Arrays.asList(new Document().append(DeviceTokenDbField.DEVICE_TOKEN, 1),
						new Document().append(DeviceTokenDbField.DEVICE_TOKEN_ID, 1),
						new Document().append(F.USER, 1))));
	}

	private void createDatabaseIndexes(String collectionName, List<Document> tobeIndexed) {
		MongoCollection<Document> collection = this.mongoClient.getDatabase(F.NHO_DATABASE)
				.getCollection(collectionName);
		for (Document index : collection.listIndexes()) {
			index = (Document) index.get(F.KEY);
			List<Integer> markToRemove = new ArrayList<>();
			for (int i = 0; i < tobeIndexed.size(); i++) {
				if (tobeIndexed.get(i).equals(index)) {
					markToRemove.add(i);
				}
			}
			if (markToRemove.size() > 0) {
				while (markToRemove.size() > 0) {
					tobeIndexed.remove(markToRemove.remove(markToRemove.size() - 1).intValue());
				}
			}

			if (tobeIndexed.size() == 0) {
				break;
			}
		}
		for (Document index : tobeIndexed) {
			getLogger().debug("create index: " + index);
			collection.createIndex(index);
		}
	}
}
