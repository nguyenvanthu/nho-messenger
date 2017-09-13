package com.nho.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.bson.Document;

import com.hazelcast.core.HazelcastInstance;
import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.mario.entity.message.SocketMessage;
import com.mario.gateway.socket.SocketSession;
import com.mario.schedule.ScheduledCallback;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.db.models.ModelFactory;
import com.nhb.messaging.rabbit.producer.RabbitMQRPCProducer;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.PingRequest;
import com.nho.message.request.Request;
import com.nho.message.response.ErrorEvent;
import com.nho.message.response.connection.PongEvent;
import com.nho.server.annotation.AnnotationLoader;
import com.nho.server.data.user.NhoHazelcastInitializer;
import com.nho.server.entity.avatar.AvatarManager;
import com.nho.server.entity.user.User;
import com.nho.server.entity.user.UserManager;
import com.nho.server.exception.ScheduledException;
import com.nho.server.exception.UserNotLoggedInException;
import com.nho.server.processors.NhoCommandRouter;
import com.nho.server.reporter.AbstractReporter;
import com.nho.server.reporter.BellHolder;
import com.nho.server.security.SecurityFactory;
import com.nho.server.security.impl.AesSecurity;
import com.nho.server.statics.Counter;
import com.nho.server.statics.DBF;
import com.nho.server.task.Timer;
import com.nho.server.task.impl.ALertTask;
import com.nho.server.task.impl.CreateBotNhoTask;
import com.nho.server.task.impl.GhostHunter;
import com.nho.statics.Error;
import com.nho.statics.F;
import com.nho.uams.client.UAMSClient;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

public class NhoServer extends BaseMessageHandler {

	private UserManager userManager;
	private HazelcastInstance hazelcast;

	private NhoCommandRouter commandRouter;
	private ModelFactory modelFactory;
	private MongoClient mongoClient;

	private AvatarManager avatarManager;
	private Map<String, Long> lastPingTimes = new ConcurrentHashMap<>();

	private SecurityFactory securityFactory;
	private AbstractReporter reporter;
	private RabbitMQRPCProducer friendProducer;
	private RabbitMQRPCProducer chatProducer;
	private RabbitMQRPCProducer notificationProducer;
	private RabbitMQRPCProducer loggingProducer;
	private UAMSClient uamsClient;
	private String applicationId;

	@Override
	public void init(PuObjectRO initParams) {

		getLogger().debug("nhoServer is starting .....");

		ModelFactory modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());

		modelFactory.setDbAdapter(getApi().getDatabaseAdapter(initParams.getString(F.MYSQL)));
		mongoClient = getApi().getMongoClient(initParams.getString(F.MONGODB));
		NhoHazelcastInitializer initializer = new NhoHazelcastInitializer(
				this.mongoClient.getDatabase(DBF.NHO_DATABASE), 1, 1000);
		this.hazelcast = getApi().getHazelcastInstance(initParams.getString(F.HAZELCAST), initializer);
		modelFactory.setMongoClient(mongoClient);
		modelFactory.setHazelcast(this.hazelcast);
		initDatabase();

		this.securityFactory = new SecurityFactory(new AesSecurity());

		this.friendProducer = getApi().getProducer("friend_rabbitmq_producer");
		this.chatProducer = getApi().getProducer("chat_rabbitmq_producer");
		this.notificationProducer = getApi().getProducer("notification_rabbitmq_producer");
		this.loggingProducer = getApi().getProducer("logging_rabbitmq_producer");

		this.modelFactory = modelFactory;
		this.userManager = new UserManager(this, hazelcast);
		this.reporter = new BellHolder(this);
		this.applicationId = initParams.getString(F.APPLICATION_ID);
//		this.uamsClient = new UAMSClient(initParams.getString(F.APPLICATION_ID), getApi().getProducer("uams_producer"));
		this.commandRouter = new NhoCommandRouter(this);
		try {
			this.commandRouter.init(AnnotationLoader.load("com.nho.server.processors"));
		} catch (Exception exception) {
			getLogger().debug("init command error ");
			return;
		}

		this.avatarManager = new AvatarManager();
		createBotNho();
//		createKafKaTopic(initParams.getString(F.TOPIC), 2, 1);
		getLogger().debug("nhoServer is ready .....");
//		this.getUserManager().addUserOnline(BotNho.USER_NAME);
		ghostHunter();
		trackingServer();
	}

	private void trackingServer() {
		getApi().getScheduler().scheduleAtFixedRate(Timer.DELAY_ALERT, Timer.PERIOD_ALERT, new ScheduledCallback() {

			@Override
			public void call() {
				try {
					ALertTask alerter = new ALertTask(NhoServer.this);
					alerter.run();
				} catch (Exception exception) {
					getLogger().debug("exception when run alert task ");
				}

			}
		});
	}

	private void ghostHunter() {
		// kill disconnected connections
		getApi().getScheduler().scheduleAtFixedRate(Timer.DELAY, Timer.PERIOD, new ScheduledCallback() {

			@Override
			public void call() {
				try {
					GhostHunter ghostHunter = new GhostHunter(NhoServer.this);
					ghostHunter.run();
				} catch (Exception exception) {
					throw new ScheduledException(exception.getMessage(), exception.getCause(), NhoServer.this);
				}
			}
		});
	}

	private void createBotNho() {
		CreateBotNhoTask createBotNhoTask = new CreateBotNhoTask(this);
		createBotNhoTask.run();
		getLogger().debug("I'm BOT ....");
	}

	@Override
	public void destroy() throws Exception {
	}

	@Override
	public PuElement handle(Message message) {
		if (message instanceof SocketMessage) {
			final SocketMessage socketMessage = (SocketMessage) message;
			String sessionId = socketMessage.getSessionId();

			switch (socketMessage.getSocketMessageType()) {
			case OPENED:
				getLogger().debug("New session opened: " + sessionId);
				// this.getPushNotificationManager().addAuthenticationAndroidPlatform();
				// if (this.getUserManager().getUserBySessionId(sessionId) !=
				// null) {
				// this.getUserManager()
				// .addUserOnline((this.getUserManager().getUserBySessionId(sessionId).getUserName()));
				// }
				break;
			case CLOSED:
				getLogger().debug("Session closed: " + sessionId);
				User user = getUserManager().getUserBySessionId(sessionId);
				if (user == null) {
					break;
				}
				this.getUserManager().whenUserDisconect(user.getUserName(), sessionId);
				break;
			case MESSAGE:
				PuElement data = message.getData();
				if (data instanceof PuArray) {
					PuArray arr = (PuArray) data;
					NhoMessage req = NhoMessage.deserialize(arr, sessionId);
					if (req instanceof PingRequest) {
						PongEvent pong = new PongEvent();
						pong.setId(((PingRequest) req).getId());
						long currentTime = System.currentTimeMillis();
						this.lastPingTimes.put(req.getSessionId(), currentTime);
						Counter.incrementInteraction();
						// getLogger().debug("got pong..." + ((PingRequest)
						// req).getId());
						this.send(pong, sessionId);

					} else if (req instanceof Request) {
						try {
							this.commandRouter.process((Request) req);
						} catch (UserNotLoggedInException userNotLoggedInException) {
							sendError(Error.USER_NOT_LOGGED_IN, "Action require login", sessionId);
						}
					}
				}
				break;
			}
		}
		return null;
	}
	
	@Override
	public PuElement interop(PuElement requestParams) {
		PuObject data = (PuObject) requestParams;
		if (data.variableExists(F.COMMAND)) {
			String command = data.getString(F.COMMAND);
			String sessionId = "";
			PuArray array = new PuArrayList();
			if(command.equals("fakeLogin")){
				String userName = data.getString("userName");
				sessionId = data.getString("sessionId");
				array.addFrom(MessageType.FAKE_LOGIN);
				array.addFrom(userName);
			}
			NhoMessage req = NhoMessage.deserialize(array, sessionId);
			try {
				this.commandRouter.process((Request) req);
			} catch (UserNotLoggedInException userNotLoggedInException) {
				sendError(Error.USER_NOT_LOGGED_IN, "Action require login", sessionId);
			}
		}
		return PuObject.fromObject(new MapTuple<>(F.STATUS, 1));
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public AvatarManager getAvatarManager() {
		return this.avatarManager;
	}

	public void send(NhoMessage message, String... sessionIds) {
		for (String sessionId : sessionIds) {
			SocketSession socketSession = getApi().getSocketSession(sessionId);
			if (socketSession != null) {
				socketSession.send(message.serialize());
			}
		}
	}

	public void send(NhoMessage message, Collection<String> users) {
		if (message != null && users != null) {
			this.send(message, users.toArray(new String[users.size()]));
		}
	}

	public void sendError(Error error, String details, String... sessionIds) {
		ErrorEvent errorEvent = new ErrorEvent();
		errorEvent.setError(error);
		errorEvent.setMessage(details);
		this.send(errorEvent, sessionIds);
	}

	public void sendError(Error error, String details, Collection<String> sessionIds) {
		if (error != null && sessionIds != null) {
			this.sendError(error, details, sessionIds.toArray(new String[sessionIds.size()]));
		}
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public HazelcastInstance getHazelcast() {
		return this.hazelcast;
	}

	public Map<String, Long> getLastPingTimes() {
		return lastPingTimes;
	}

	public void setLastPingTimes(Map<String, Long> lastPingTimes) {
		this.lastPingTimes = lastPingTimes;
	}

	public void closeSession(String sessionId) {
		if (this.lastPingTimes.containsKey(sessionId)) {
			this.lastPingTimes.remove(sessionId);
			getLogger().debug("remove sessionId " + sessionId);
		}
		SocketSession session = this.getApi().getSocketSession(sessionId);
		if (session != null) {
			try {
				session.close();
				// if (latch != null) {
				// latch = null;
				// }
			} catch (IOException e) {
				getLogger().error("cannot close session", e);
			}
		}
	}

	public String getSessionIdByLastPingTime(long lastTime) {
		String session = null;
		for (Entry<String, Long> entry : this.lastPingTimes.entrySet()) {
			if (entry.getValue() == lastTime) {
				return entry.getKey();
			}
		}
		return session;
	}

	public RabbitMQRPCProducer getFriendProducer() {
		return this.friendProducer;
	}

	public RabbitMQRPCProducer getChatProducer() {
		return this.chatProducer;
	}

	public RabbitMQRPCProducer getNotificationProducer() {
		return this.notificationProducer;
	}

	public RabbitMQRPCProducer getLoggingProducer() {
		return this.loggingProducer;
	}

	public SecurityFactory getSecurityFactory() {
		return this.securityFactory;
	}

	public AbstractReporter getReporter() {
		return this.reporter;
	}

	public UAMSClient getUAMSClient() {
		return this.uamsClient;
	}

	public String getApplicationId() {
		return this.applicationId;
	}

	private void initDatabase() {
		createDatabaseIndexes(F.USER_COLLECTION, new ArrayList<>(Arrays.asList(new Document().append(F.USERNAME, 1),
				new Document().append(F.CHECKSUM, 1), new Document().append(DBF.FACE_TOKEN, 1))));
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

	@SuppressWarnings("unused")
	private void createKafKaTopic(String topic, int partitions, int replications) {
		ZkClient zkClient = null;
		ZkUtils zkUtils = null;
		try {
			String zookeeperHosts = "localhost:2181";
			int sessionTimeOut = 15 * 1000;
			int connectionTimeOut = 10 * 1000;
			zkClient = new ZkClient(zookeeperHosts, sessionTimeOut, connectionTimeOut, ZKStringSerializer$.MODULE$);
			zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);

			Properties topicConfiguration = new Properties();

			if (!AdminUtils.topicExists(zkUtils, topic)) {
				AdminUtils.createTopic(zkUtils, topic, partitions, replications, topicConfiguration, null);
				getLogger().debug("create topic in kafka success");
			} else {
				getLogger().debug("topic is already created");
			}
		} catch (Exception exception) {
			getLogger().debug("error when create kafka topic", exception);
		}
	}
}
