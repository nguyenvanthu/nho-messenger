package com.nho.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
import com.nho.chat.annotation.AnnotationLoader;
import com.nho.chat.entity.ChannelManager;
import com.nho.chat.entity.ChatManager;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatCommandRouter;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChannelDBField;
import com.nho.chat.statics.Timer;
import com.nho.statics.F;

public class ChatHandler extends BaseMessageHandler {
	private ChatCommandRouter commandRouter;
	private HazelcastInstance hazelcast;
	private ModelFactory modelFactory;
	private ChannelManager channelManager;
	private ChatManager chatManager;
	private MongoClient mongoClient;

	@Override
	public void init(PuObjectRO initParams) {
		super.init(initParams);
		getLogger().debug("statrting ChatServer ....");
		ModelFactory modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		mongoClient = getApi().getMongoClient(initParams.getString(F.MONGODB));
		this.hazelcast = getApi().getHazelcastInstance(initParams.getString(F.HAZELCAST));
		modelFactory.setMongoClient(mongoClient);
		modelFactory.setHazelcast(hazelcast);
		this.modelFactory = modelFactory;
		initDatabase();
		this.channelManager = new ChannelManager();
		this.chatManager = new ChatManager();
		this.commandRouter = new ChatCommandRouter(this);
		try {
			commandRouter.init(AnnotationLoader.load("com.nho.chat.router"));
		} catch (Exception e) {
			getLogger().debug("error when register command processor in Chat Server");
			throw new RuntimeException(e);
		}
		refreshPokeTimes();
	}

	private void refreshPokeTimes() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				ChatManager manager = new ChatManager();
				manager.deletePingTimes();
			}
		}, getDelayTime(), Timer.PERIOD, TimeUnit.MILLISECONDS);
	}

	private long getDelayTime() {
		long time = 0;
		DateTime date = new DateTime().withZone(DateTimeZone.forID(Timer.TIME_ZONE));
		int currentHour = date.getHourOfDay();
		if (currentHour < 6) {
			DateTime nextTime = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 6, 0,
					DateTimeZone.forID(Timer.TIME_ZONE));
			time = nextTime.getMillis() - date.getMillis();
		} else {
			DateTime nextDay = date.plusDays(1);
			DateTime nextTime = new DateTime(nextDay.getYear(), nextDay.getMonthOfYear(), nextDay.getDayOfMonth(), 6, 0,
					DateTimeZone.forID(Timer.TIME_ZONE));
			time = nextTime.getMillis() - date.getMillis();
		}
		return time;
	}

	@Override
	public PuElement handle(Message message) {

		PuObject data = (PuObject) message.getData();
		if (data.variableExists(F.COMMAND)) {
			ChannelCommand command = ChannelCommand.fromCode(data.getInteger(F.COMMAND));
			try {
				return this.commandRouter.process(command, data);
			} catch (ChatException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		PuObject data = (PuObject) requestParams;
		if (data.variableExists(F.COMMAND)) {
			ChannelCommand command = ChannelCommand.fromCode(data.getInteger(F.COMMAND));
			try {
				return this.commandRouter.process(command, data);
			} catch (ChatException e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public ChannelManager getChannelManager() {
		return this.channelManager;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public HazelcastInstance getHazelcast() {
		return this.hazelcast;
	}

	private void initDatabase() {
		createDatabaseIndexes(F.CHANNEL_COLLECTION,
				new ArrayList<>(Arrays.asList(new Document().append(F.USER_NAMES, 1), new Document().append(F.ID, 1))));
		createDatabaseIndexes(ChannelDBField.LIVE_OBJECT_COLLECTION,
				new ArrayList<>(Arrays.asList(new Document().append(ChannelDBField.OBJ_ID, 1))));
		createDatabaseIndexes(ChannelDBField.LIVE_OBJECT_COLLECTION,
				new ArrayList<>(Arrays.asList(new Document().append(ChannelDBField.CHANNEL_ID, 1))));
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
