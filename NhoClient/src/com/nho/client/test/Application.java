package com.nho.client.test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nhb.common.BaseLoggable;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuObject;
import com.nhb.common.utils.Initializer;
import com.nhb.common.vo.HostAndPort;
import com.nhb.common.vo.UserNameAndPassword;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;
import com.nhb.eventdriven.impl.BaseEventHandler;
import com.nho.client.NhoClient;
import com.nho.client.NhoEvent;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.PingRequest;
import com.nho.message.request.channel.CreateChannelRequest;
import com.nho.message.request.channel.JoinChannelRequest;
import com.nho.message.request.channel.PokeRequest;
import com.nho.message.request.channel.StateAppChangeRequest;
import com.nho.message.request.chat.ChatMessage;
import com.nho.message.request.friend.AcceptFriendRequest;
import com.nho.message.request.friend.BlockFriendRequest;
import com.nho.message.request.friend.CancelFriendRequest;
import com.nho.message.request.friend.GetListFriend;
import com.nho.message.request.friend.GetListInviteFriend;
import com.nho.message.request.friend.IgnoreFriendRequest;
import com.nho.message.request.friend.SendFriendRequest;
import com.nho.message.request.login.LoginFacebookRequest;
import com.nho.message.request.login.LoginRequest;
import com.nho.message.request.login.LogoutRequest;
import com.nho.message.request.notification.RegisterPushNotificationRequest;
import com.nho.message.request.notification.TestPushNotification;
import com.nho.message.response.ErrorEvent;
import com.nho.message.response.channel.ChatInvitationResponse;
import com.nho.message.response.channel.InvitedToChatEvent;
import com.nho.message.response.channel.PokeReponse;
import com.nho.message.response.connection.ConnectionResponse;
import com.nho.message.response.friend.AcceptFriendResponse;
import com.nho.message.response.friend.CancelFriendResponse;
import com.nho.message.response.friend.GetListFriendResponse;
import com.nho.message.response.friend.GetListInviteFriendResponse;
import com.nho.message.response.friend.IgnoreFriendResponse;
import com.nho.message.response.friend.ListStateFriendResponse;
import com.nho.message.response.friend.SendFriendResponse;
import com.nho.message.response.friend.StateFriendChangeResponse;
import com.nho.message.response.notification.RegisterPushNotificationResponse;
import com.nho.statics.AppState;
import com.nho.statics.AvatarType;
import com.nho.statics.BlockFriendResponse;
import com.nho.statics.ChannelType;
import com.nho.statics.F;
import com.nho.statics.StatusFriend;
import com.nho.statics.StatusUser;

public class Application extends BaseLoggable {
	private static final String TOKEN = "APA91bEned4VRoY6Fu9RkLjck6Ir54u62-zZuuTa-h7yiCHWMO5ILheL5KtpzG-RVbhhxlse9kvM0SP-t1O9htzqD_yIbXR8mg0XQKKY-gvSV5CFEkBXFqR1g4mpaEferdsqrU33WqGX";
	private static final String UDID = UUID.randomUUID().toString();
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	// Bootstrap main method
	public static void main(String[] args) {
		Initializer.bootstrap(Application.class);

		Application app = new Application(args);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			{
				this.setPriority(MAX_PRIORITY);
				this.setName("Shutdown thread");
			}

			@Override
			public void run() {
				app.stop();
			}
		});
		app.start();
	}

	private void stressTest() {
		for (int i = 0; i < 1000; i++) {

		}
	}
	// Application

	@SuppressWarnings("unused")
	private DecimalFormat df = new DecimalFormat("0.##");
	private ScheduledExecutorService timer = Executors.newScheduledThreadPool(2);
	private NhoClient nhoClient;
	private UserNameAndPassword loginInfo;

	private String channelId = null;

	private String userToInvite = null;

	private EventHandler onConnectionResponse = new BaseEventHandler(this, "onConnectionResponse");
	private EventHandler onDisconnect = new BaseEventHandler(this, "onDisconnect");
	private EventHandler onLoginResponse = new BaseEventHandler(this, "onLoginResponse");
	private EventHandler onError = new BaseEventHandler(this, "onError");
	private EventHandler onPong = new BaseEventHandler(this, "onPong");
	private EventHandler onInvitedToChatEvent = new BaseEventHandler(this, "onInvitedToChatEvent");
	private EventHandler onChatInvitationResponse = new BaseEventHandler(this, "onChatInvitationResponse");
	private EventHandler onChat = new BaseEventHandler(this, "onChat");

	private EventHandler onSendFriendRequestResponse = new BaseEventHandler(this, "onSendFriendRequestResponse");
	private EventHandler onGetListFriendResponse = new BaseEventHandler(this, "onGetListFriendResponse");
	private EventHandler onAcceptFriendResponse = new BaseEventHandler(this, "onAcceptFriendResponse");
	private EventHandler onIgnoreFriendResponse = new BaseEventHandler(this, "onIgnoreFriendResponse");
	private EventHandler onCancelFriendResponse = new BaseEventHandler(this, "onCancelFriendResponse");
	private EventHandler onGetListFriendRequestReceiveResponse = new BaseEventHandler(this,
			"onGetListFriendRequestReceiveResponse");
	private EventHandler onBlockFriendResponse = new BaseEventHandler(this, "onBlockFriendResponse");

	private EventHandler onCreateApplicationResponse = new BaseEventHandler(this, "onCreateApplicationResponse");
	private EventHandler onLogoutResponse = new BaseEventHandler(this, "onLogoutResponse");
	private EventHandler onRegisterPushNotification = new BaseEventHandler(this, "onRegisterPushNotification");
	private EventHandler onRegisterResponse = new BaseEventHandler(this, "onRegisterResponse");
	private EventHandler onStateFriendChange = new BaseEventHandler(this, "onStateFriendChange");
	private EventHandler onGetListStateFriend = new BaseEventHandler(this, "onGetListStateFriend");
	private EventHandler onPokeResponse = new BaseEventHandler(this, "onPokeResponse");

	private Application(String[] args) {

		this.nhoClient = new NhoClient(new HostAndPort(System.getProperty("server.host", "139.162.5.38"),
				Integer.valueOf(System.getProperty("server.port", "9999"))), true);

		System.out.println("connecting to: " + this.nhoClient.getServerAddress());

		this.loginInfo = new UserNameAndPassword(
				args.length > 0 ? args[0] : System.getProperty("chat.user.name", "bachden"),
				args.length > 1 ? args[1] : System.getProperty("chat.user.password", "123456"));
		if (args.length > 2) {
			userToInvite = args[2];
		}

		nhoClient.addEventListener(MessageType.CONNECTION_RESPONSE, this.onConnectionResponse);
		nhoClient.addEventListener(MessageType.DISCONNECT_EVENT, this.onDisconnect);
		nhoClient.addEventListener(MessageType.LOGIN_RESPONSE, this.onLoginResponse);
		nhoClient.addEventListener(MessageType.INVITED_TO_CHAT_EVENT, this.onInvitedToChatEvent);
		nhoClient.addEventListener(MessageType.CHAT_INVITATION_RESPONSE, this.onChatInvitationResponse);
		nhoClient.addEventListener(MessageType.CHAT, this.onChat);
		nhoClient.addEventListener(MessageType.PONG, this.onPong);
		nhoClient.addEventListener(MessageType.ERROR, this.onError);

		nhoClient.addEventListener(MessageType.SEND_FRIEND_RESPONSE, this.onSendFriendRequestResponse);
		nhoClient.addEventListener(MessageType.GET_LIST_FRIEND_PENDDING_RESPONSE, this.onGetListFriendResponse);
		nhoClient.addEventListener(MessageType.ACCEPT_FRIEND_RESPONSE, onAcceptFriendResponse);
		nhoClient.addEventListener(MessageType.IGNORE_FRIEND_RESPONSE, onIgnoreFriendResponse);
		nhoClient.addEventListener(MessageType.CANCEL_FRIEND_RESPONSE, onCancelFriendResponse);
		nhoClient.addEventListener(MessageType.GET_LIST_INVITE_FRIEND_RESPONSE, onGetListFriendRequestReceiveResponse);
		nhoClient.addEventListener(MessageType.BLOCK_FRIEND_RESPONSE, onBlockFriendResponse);

		nhoClient.addEventListener(MessageType.CREATE_APPLICATION_RESPONSE, onCreateApplicationResponse);
		nhoClient.addEventListener(MessageType.LOGOUT_RESPONSE, onLogoutResponse);
		nhoClient.addEventListener(MessageType.REGISTER_PUSH_NOTIFICATION_RESPONSE, onRegisterPushNotification);

		nhoClient.addEventListener(MessageType.STATE_FRIEND_CHANGE, onStateFriendChange);
		nhoClient.addEventListener(MessageType.LIST_STATE_FRIEND_RESPONSE, onGetListStateFriend);
		nhoClient.addEventListener(MessageType.POKE_RESPONSE, onPokeResponse);
	}

	private void start() {
		try {
			nhoClient.connect();
		} catch (IOException e) {
			// connect fail
			getLogger().error("Unable to connect", e);
			System.exit(1);
		}
	}

	private void stop() {
		timer.shutdown();
		nhoClient.close();
	}

	/************************ Logic Methods ************************/

	private void login() {
		LoginFacebookRequest request = new LoginFacebookRequest();
		request.setFacebookId("107012086424176");
		request.setFacebookToken(
				"EAAQSBTkX0HQBAN0soBmJelCavCZAHzCek4ZACQqworofD7UyCHuhrsZBEZAZC6wY5fnJBkNS0xvadaZCaeIAXaWBJrGoZAvy5s8nOgR07rwRf0KzUZCnquwWOMx80I8oxQ6UZACw86kg78OpKpPKsKA1tldikwTMrzE1MiXljySfICNVmynYOEdjPKq8PIqGO2S8IbxWsL5VhLEi2jSLmd5TF");
		this.nhoClient.send(request);
	}

	private void logout() {
		getLogger().debug("start logout ");
		LogoutRequest request = new LogoutRequest();
		this.nhoClient.send(request);
	}

	private void getListFriend(String senderUserName) {
		getLogger().debug("start get list friend of tuanhung");
		GetListFriend request = new GetListFriend();
		request.setSenderUserName(senderUserName);
		// getListFriendRequest.setUserId(Converter.uuidToBytes(UUID.fromString("3C608231-E543-4FBD-8B60-45F79DB08DDD")));
		request.setStatus(StatusFriend.ACCEPT);
		this.nhoClient.send(request);
	}

	private void sendFriendRequest() {
		SendFriendRequest sendFriendRequest = new SendFriendRequest();
		sendFriendRequest.setStatusFriend(StatusFriend.PENDING);
		sendFriendRequest.setReceiverUserName("oanhnk");
		sendFriendRequest.setSenderUserName("thunv");
		// sendFriendRequest.setSenderObjectId(new
		// ObjectId("56d3feb1c0beff286968bd75"));
		// sendFriendRequest.setReceiverId(Converter.uuidToBytes(UUID.fromString("3C608231-E543-4FBD-8B60-45F79DB08DDD")));
		getLogger().debug("thunv send friend request to bachden");
		this.nhoClient.send(sendFriendRequest);
	}

	private void acceptFriendrequest() {
		getLogger().debug("accept friend request...");
		AcceptFriendRequest acceptFriendRequest = new AcceptFriendRequest();
		acceptFriendRequest.setStatusFriend(StatusFriend.ACCEPT);
		acceptFriendRequest.setAccepterUserName("oanhnk");
		acceptFriendRequest.setSenderUserName("thunv");

		this.nhoClient.send(acceptFriendRequest);
	}

	private void ignoreFriendRequest() {
		getLogger().debug("ignore friend request...");
		IgnoreFriendRequest ignoreFriendRequest = new IgnoreFriendRequest();
		ignoreFriendRequest.setStatusFriend(StatusFriend.IGNORE);
		// ignoreFriendRequest.setSenderId(Converter.uuidToBytes(UUID.fromString("8FC44DA7-91F9-4D4D-896B-9976E3D90A25")));
		// ignoreFriendRequest
		// .setIgnorerId(Converter.uuidToBytes(UUID.fromString("3C608231-E543-4FBD-8B60-45F79DB08DDD")));
		this.nhoClient.send(ignoreFriendRequest);
	}

	private void cancelFriend() {
		getLogger().debug("cancel friend ...");
		CancelFriendRequest cancelFriendRequest = new CancelFriendRequest();
		cancelFriendRequest.setStatusFriend(StatusFriend.IGNORE);
		cancelFriendRequest.setCancelerUserName("thunv");
		cancelFriendRequest.setSenderUserName("bachden");
		// cancelFriendRequest.setSenderId(Converter.uuidToBytes(UUID.fromString("8FC44DA7-91F9-4D4D-896B-9976E3D90A25")));
		// cancelFriendRequest
		// .setCancelerId(Converter.uuidToBytes(UUID.fromString("3C608231-E543-4FBD-8B60-45F79DB08DDD")));
		this.nhoClient.send(cancelFriendRequest);
	}

	private void blockFriend() {
		getLogger().debug("block friend ...");
		BlockFriendRequest request = new BlockFriendRequest();
		request.setBlockerUserName("thunv");
		request.setSenderUserName("oanhnk");
		request.setStatusFriend(StatusFriend.BLOCK);
		// blockFriendRequest.setSenderId(Converter.uuidToBytes(UUID.fromString("8FC44DA7-91F9-4D4D-896B-9976E3D90A25")));
		// blockFriendRequest.setBlockerId(Converter.uuidToBytes(UUID.fromString("3C608231-E543-4FBD-8B60-45F79DB08DDD")));
		this.nhoClient.send(request);
	}

	private void getListFriendRequestReceive() {
		getLogger().debug("get list friend request receive ...");
		GetListInviteFriend getListFriendRequestReceive = new GetListInviteFriend();
		getListFriendRequestReceive.setStatusFriend(StatusFriend.PENDING);
		getListFriendRequestReceive.setUserName("bachden");
		this.nhoClient.send(getListFriendRequestReceive);
	}

	private void ping() {
		PingRequest ping = new PingRequest();
		System.out.println("time send ping: " + System.currentTimeMillis());
		this.nhoClient.send(ping);
	}

	private void registerPushNotification() {
		getLogger().debug("register push notification to server");
		RegisterPushNotificationRequest request = new RegisterPushNotificationRequest();
		request.setToken(TOKEN);

		this.nhoClient.send(request);
	}

	private void createChannelChat(String inviter, String receiver) {
		CreateChannelRequest createChannelRequest = new CreateChannelRequest();
		createChannelRequest.setChannelType(ChannelType.PRIVATE);
		createChannelRequest.addInvitedUsers(receiver);
		createChannelRequest.setMessage(PuObject.fromObject(new MapTuple<>(F.VALUE, 0)));
		this.nhoClient.send(createChannelRequest);
		getLogger().debug("user {} invite user {} to chat ", inviter, receiver);
	}

	private void sendStateApp() {
		StateAppChangeRequest request = new StateAppChangeRequest();
		request.setDeviceToken(TOKEN);
		request.setState(AppState.RESUME);
		this.nhoClient.send(request);
	}

	private void poke() {
		PokeRequest request = new PokeRequest();
		this.nhoClient.send(request);
	}

	private void monitorLag() {
		timer.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				ping();
			}
		}, 5, 5, TimeUnit.SECONDS);
	}

	/************************ Event Handlers ************************/
	private <T extends NhoMessage> T getMessage(Event event) {
		NhoEvent ulaaiEvent = (NhoEvent) event;
		return ulaaiEvent.getMessage();
	}

	@Deprecated
	public void onConnectionResponse(Event event) {
		ConnectionResponse response = getMessage(event);
		// this.monitorLag();
		getLogger().debug("Connect " + (response.isSuccessful() ? "success" : "failure"));
		if (response.isSuccessful()) {
			this.login();
			// this.monitorLag();
		} else {
			System.exit(1);
		}
	}

	@Deprecated
	public void onError(Event event) {
		ErrorEvent errorEvent = getMessage(event);
		switch (errorEvent.getError()) {
		case CHANNEL_FULL:
			break;
		case CHANNEL_NOT_FOUND:
			break;
		case CREATE_ACCOUNT_ERROR:
			// if account already registered, try to login
			getLogger().debug(
					"create account fail, there may be already exiting an account with the same userName, attempting to login");
			this.login();
			break;
		case INVALID_LOGIN:
			break;
		case OK:
			break;
		case USER_ALREADY_IN_CHANNEL:
			break;
		case USER_NOT_JOINED_TO_CHANNEL:
			break;
		case USER_NOT_LOGGED_IN:
			break;
		default:
			break;

		}
	}

	@Deprecated
	public void onChatInvitationResponse(Event event) {
		ChatInvitationResponse chatInvitationResponse = getMessage(event);
		getLogger().debug("Chat invitation delivered, invited users: " + chatInvitationResponse.getInvitedUsers());
		this.channelId = chatInvitationResponse.getChannelId();
		getLogger().debug(" --> channel id: " + chatInvitationResponse.getChannelId());
	}

	@Deprecated
	public void onInvitedToChatEvent(Event event) {
		InvitedToChatEvent invitation = getMessage(event);
		String channelId = invitation.getChannelId();

		PuObject invitationMessage = invitation.getMessage();
		// int value = 0;
		// if (invitationMessage != null) {
		// value = invitationMessage.getInteger(F.VALUE, 0);
		// }

		PuObject joinMessage = new PuObject();
		String data = "123 \n 456";
		joinMessage.setString(F.VALUE, data);

		getLogger().debug("Got chat invitation from " + invitation.getSender() + " with message: " + invitationMessage
				+ " --> sending join channel with message: " + joinMessage);

		JoinChannelRequest joinChannelRequest = new JoinChannelRequest();
		joinChannelRequest.setChannelId(channelId);
		joinChannelRequest.setMessage(joinMessage);

		this.nhoClient.send(joinChannelRequest);
	}

	@Deprecated
	public void onChat(Event event) {
		ChatMessage chatMessage = getMessage(event);
		getLogger().debug("to is " + chatMessage.getTo());
		getLogger().debug("channel Id " + chatMessage.getTo());
		if (chatMessage.getFrom().equals(this.loginInfo.getUserName())) {
			getLogger().debug("Message delivered");
			return;
		}
		getLogger().debug("Got chat message from {}: {} {}", chatMessage.getFrom(),
				PuObject.fromObject(chatMessage.getData()).getString(F.VALUE));
		String value = chatMessage.getData().getString(F.VALUE);
		try {
			Thread.sleep(1000);
			ChatMessage reply = new ChatMessage();
			reply.autoMessageId();
			reply.autoSentTime();
			reply.setTo(chatMessage.getTo());
			reply.setData(PuObject.fromObject(new MapTuple<>(F.VALUE, value + "\n 789")));
			// getLogger().debug("value PuObject "+PuObject.fromObject(new
			// MapTuple<>(F.VALUE, value)).getString(F.VALUE));
			getLogger().debug("Reply with message: " + reply.getData() + "message Id " + reply.getMessageId());
			this.nhoClient.send(reply);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void onDisconnect(Event event) {
		getLogger().debug("Disconnected, stop application");
		// start();
		// System.exit(0);
	}

	@Deprecated
	public void onPong(Event event) {
		// PongEvent pong = getMessage(event);
		System.out.println("time recei pong: " + System.currentTimeMillis());
		// getLogger().info("Network delay time {}ms",
		// df.format(pong.getDelayMillis()));
	}

	@Deprecated
	public void onSendFriendRequestResponse(Event event) {
		getLogger().debug("receive new friend request  ....");
		SendFriendResponse response = new SendFriendResponse();
		response = getMessage(event);
		if (response.isSuccessful()) {
			getLogger().debug("recieved request friend from user ");
			getLogger().debug("sender " + response.getSenderUserName());
			getLogger().debug("receiver " + response.getReceiverUserName());
			acceptFriendrequest();
			// ignoreFriendRequest();
		} else {
			getLogger().debug("send friend request failed ");
		}
	}

	@Deprecated
	public void onGetListFriendResponse(Event event) {
		getLogger().debug("receive list friend ....");
		GetListFriendResponse response = new GetListFriendResponse();
		response = getMessage(event);
		if (response.isSuccessful()) {
			getLogger().debug("number friend is " + response.getDisplayNames().size());
			for (String friendName : response.getUsernames()) {
				getLogger().debug("friend " + friendName);
			}

			// getLogger().debug("number message in response
			// :"+response.getMessages().size());
		} else {
			getLogger().debug("something wrong ");
		}
	}

	@Deprecated
	public void onAcceptFriendResponse(Event event) {
		getLogger().debug("accept friend request response ");
		AcceptFriendResponse response = new AcceptFriendResponse();
		response = getMessage(event);
		getLogger().debug("accepter is " + response.getAccepterUserName());
		blockFriend();
		if (response.isSuccessful() == true) {
			getLogger().debug("success");
			getListFriend(response.getAccepterUserName());
			getListFriend(response.getSenderUserName());
		} else {
			getLogger().debug("something wrong ");
		}
	}

	@Deprecated
	public void onIgnoreFriendResponse(Event event) {
		getLogger().debug("ignore friend request response ");
		IgnoreFriendResponse response = new IgnoreFriendResponse();
		response = getMessage(event);
		// getLogger().debug("user {} ignore friend request from {}",
		// Converter.bytesToUUID(response.getIgnorerId()),
		// Converter.bytesToUUID(response.getSenderId()));
		getLogger().debug("status " + response.getStatusFriend());
		getLogger().debug("success " + response.isSuccessful());
		if (response.isSuccessful() == true) {
			getLogger().debug("success");
		} else {
			getLogger().debug("something wrong ");
		}
	}

	@Deprecated
	public void onCancelFriendResponse(Event event) {
		getLogger().debug("cancel friend response ");
		CancelFriendResponse response = new CancelFriendResponse();
		response = getMessage(event);
		// getLogger().debug("user {} cancel friend request from {}",
		// Converter.bytesToUUID(response.getCancerId()),
		// Converter.bytesToUUID(response.getSenderId()));
		getLogger().debug("status " + response.getStatusFriend());
		getLogger().debug("success " + response.isSuccessful());
		if (response.isSuccessful() == true) {
			getLogger().debug("success");
		} else {
			getLogger().debug("something wrong ");
		}
	}

	@Deprecated
	public void onGetListFriendRequestReceiveResponse(Event event) {
		getLogger().debug("get list friend request receive ");
		GetListInviteFriendResponse response = new GetListInviteFriendResponse();
		response = getMessage(event);

		if (response.isSuccessful() == true) {
			getLogger().debug("success");
			getLogger().debug("number of friend " + response.getUsernames().size());
		} else {
			getLogger().debug("something wrong ");
		}
	}

	@Deprecated
	public void onBlockFriendResponse(Event event) {
		getLogger().debug("get block friend response ");
		BlockFriendResponse response = new BlockFriendResponse();
		response = getMessage(event);
		getLogger().debug("user is " + response.getSenderUserName());
		getLogger().debug("success " + response.isSuccessful());
		if (response.isSuccessful() == true) {
			getLogger().debug("success");
		} else {
			getLogger().debug("something wrong ");
		}
	}

	@Deprecated
	public void onCreateApplicationResponse(Event event) {
		getLogger().debug("receive response successful");
	}

	@Deprecated
	public void onLogoutResponse(Event event) {
		getLogger().debug("receive response logpout from server ");
	}

	@Deprecated
	public void onRegisterPushNotification(Event event) {
		getLogger().debug("register push notification response ");
		RegisterPushNotificationResponse response = new RegisterPushNotificationResponse();
		response = getMessage(event);
		getLogger().debug("device Token " + response.getDeviceTokenId());
		testPushNotification();
		// sendStateApp();
	}

	private void testPushNotification() {
		TestPushNotification test = new TestPushNotification();
		nhoClient.send(test);
	}

	@Deprecated
	public void onStateFriendChange(Event event) {
		StateFriendChangeResponse response = getMessage(event);
		if (response.getStatus() == StatusUser.ONLINE) {
			getLogger().debug("user {} online ", response.getUserName());
		} else {
			getLogger().debug("user {} offline ", response.getUserName());
		}
	}

	@Deprecated
	public void onGetListStateFriend(Event event) {
		getLogger().debug("receive list status friend ");
		ListStateFriendResponse response = getMessage(event);
		for (String userName : response.getUserNames()) {
			getLogger().debug("friend :" + userName);
		}
		for (StatusUser status : response.getStatuss()) {
			getLogger().debug("status : " + status.toString());
		}
	}

	@Deprecated
	public void onPokeResponse(Event event) {
		PokeReponse response = getMessage(event);
		if (!response.isSuccess()) {
			getLogger().debug("error " + response.getError().toString());
		}
	}
}
