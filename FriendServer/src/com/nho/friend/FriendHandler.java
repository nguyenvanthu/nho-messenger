package com.nho.friend;

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
import com.nho.friend.annotation.AnnotationLoader;
import com.nho.friend.exception.FriendException;
import com.nho.friend.router.FriendCommandRouter;
import com.nho.friend.statics.F;
import com.nho.friend.statics.FriendCommand;

/**
 * FriendServer handle friends command
 */
public class FriendHandler extends BaseMessageHandler {
	private FriendCommandRouter commandRouter;
	private HazelcastInstance hazelcast;
	private ModelFactory modelFactory;
	private MongoClient mongoClient;

	@Override
	public void init(PuObjectRO initParams) {
		super.init(initParams);
		getLogger().debug("friendServer is starting .....");

		this.hazelcast = getApi().getHazelcastInstance(initParams.getString(F.HAZELCAST));
		ModelFactory modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		mongoClient = getApi().getMongoClient(initParams.getString(F.MONGODB));
		modelFactory.setMongoClient(mongoClient);
		this.modelFactory = modelFactory;

		initFriendDatabase();

		commandRouter = new FriendCommandRouter(this);
		try {
			// register command by annotation
			commandRouter.init(AnnotationLoader.load("com.nho.friend.router"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PuElement handle(Message message) {
		PuObject data = (PuObject) message.getData();
		if (data.variableExists(F.COMMAND)) {
			FriendCommand command = FriendCommand.fromCode(data.getInteger(F.COMMAND));
			try {
				return this.commandRouter.process(command, data);
			} catch (FriendException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		PuObject data = (PuObject) requestParams;
		if (data.variableExists(F.COMMAND)) {
			FriendCommand command = FriendCommand.fromCode(data.getInteger(F.COMMAND));
			try {
				return this.commandRouter.process(command, data);
			} catch (FriendException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public HazelcastInstance getHazelcast() {
		return this.hazelcast;
	}

	private void initFriendDatabase() {
		createDatabaseIndexes(F.FRIEND_COLLECTION, new ArrayList<>(Arrays.asList(new Document().append(F.USER, 1),
				new Document().append(F.BUDDY, 1), new Document().append(F.STATUS, 1))));
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
