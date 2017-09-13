package com.nho.server.processors.login;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.router.impl.MakeFriendProcessor;
import com.nho.friend.router.impl.MakeFriendWithBotProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.login.LoginRequest;
import com.nho.message.response.login.LoginResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.avatar.Avatar;
import com.nho.server.entity.user.User;
import com.nho.server.exception.IdHandlerException;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.helper.NotificationHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.BotNho;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.AvatarType;
import com.nho.statics.Error;
import com.nho.statics.PNF;
import com.nho.statics.PushNotificationType;
import com.nho.statics.StatusUser;
import com.nho.uams.statics.ActivityType;

/**
 * client can login with facebookId && facebookToken or only facebookToken when
 * client login with facebookId, server must check this facebookId is exist ? ->
 * login success or not when client login with only facebookToken
 */
@NhoCommandProcessor(command = { MessageType.LOGIN })
public class LoginProcessor extends AbstractNhoProcessor<LoginRequest> {
	private static final String FACEBOOK_PERMISSIONS_URL = "https://graph.facebook.com/me?fields=permissions&access_token=";
	private static final String FACEBOOK_EMAIL_URL = "https://graph.facebook.com/me?fields=email&access_token=";
	private static final String FACEBOOK_FRIENDS_URL = "https://graph.facebook.com/v2.7/me/friends?access_token=";
	private static final String FACEBOOK_AVATAR_URL = "https://graph.facebook.com/me?fields=picture.height(200).width(200)&access_token=";

	@Override
	protected void process(LoginRequest request) throws Exception {
		IdHandlerException handlerException = new IdHandlerException();
		Thread.setDefaultUncaughtExceptionHandler(handlerException);
		LoginResponse response = new LoginResponse();
		String faceToken = request.getFacebookToken();
		getLogger().debug(" facebookToken: {}", faceToken);
		if (faceToken == null) {
			response.setSuccess(false);
			response.setError(Error.FACE_TOKEN_NOT_FOUND);
			this.send(response, request.getSessionId());
			return;
		}
		AvatarType avtType = AvatarType.ICON;
		if (request.getAvtType() != null) {
			avtType = request.getAvtType();
		}
		String avtUrl = "";
		if (request.getAvtUrl() == null) {
			avtUrl = getUserAvtUrl(faceToken);
		} else {
			avtUrl = request.getAvtUrl();
		}
		String displayName = "";
		if (request.getDisplayName() != null) {
			displayName = request.getDisplayName();
		}
		List<String> permissions = getUserPermissions(request);
		if (permissions.size() <= 0) {
			response.setSuccess(false);
			response.setError(Error.FACE_TOKEN_EXPIRED);
			this.send(response, request.getSessionId());
			return;
		}
		String faceId = permissions.get(0);

		// insert new user
		Avatar avatar = getUserAvatar(avtUrl, avtType);
		getLogger().debug("avatar name: " + avatar.getName());
		String email = "";
		if (permissions.contains("email")) {
			try {
				email = getEmail(request.getFacebookToken());
			} catch (Exception exception) {
				email = "";
			}
		}
		String objectId = this.getUserMongoModel().insert(faceId, faceToken, displayName, avatar, email);
		if (objectId == null) {
			// login fail
			response.setSuccess(false);
			response.setError(Error.CREATE_ACCOUNT_ERROR);
			this.send(response, request.getSessionId());
			return;
		}
		whenUserLoginSucess(faceId, request, permissions);
		response.setSuccess(true);
		response.setAvatarName(avtUrl);
		response.setDisplayName(displayName);
		response.setToken(faceToken);
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if (user != null) {
			getLogger().debug("user added to user manager ");
			user.setStatus(StatusUser.ONLINE);
		}
		this.send(response, request.getSessionId());
		this.sendLog(request, user);
	}

	private String getUserAvtUrl(String facebookToken) throws Exception {
		String urlAvt = null;
		URL url = new URL(FACEBOOK_AVATAR_URL + facebookToken);
		getLogger().debug("url get avatar facebook : " + FACEBOOK_AVATAR_URL + facebookToken);
		URLConnection connection = url.openConnection();
		InputStream stream;
		stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		String content = IOUtils.toString(stream, endCoding);
		JSONObject json = (JSONObject) new JSONParser().parse(content);
		JSONObject picture = (JSONObject) json.get("picture");
		JSONObject data = (JSONObject) picture.get("data");
		urlAvt = (String) data.get("url");
		getLogger().debug("url Avt: " + urlAvt);
		return urlAvt;
	}

	private Avatar getUserAvatar(String avtUrl, AvatarType avtType) {
		Avatar avt = new Avatar();
		avt.setName(avtUrl);
		avt.setType(avtType);
		avt.setUrl(avtUrl);
		return avt;
	}

	private String getEmail(String facebookToken) throws Exception {
		String email = "";
		URL url = new URL(FACEBOOK_EMAIL_URL + facebookToken);
		URLConnection connection = url.openConnection();
		InputStream stream;
		stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		PuObject puObject = PuObject.fromJSON(IOUtils.toString(stream, endCoding));
		if (puObject == null) {
			return email;
		}
		if (puObject.variableExists("error")) {
			return email;
		}
		if (puObject.variableExists("email")) {
			email = puObject.getString("email");
		}
		return email;
	}

	private List<String> getUserPermissions(LoginRequest request) throws Exception {
		String facebookToken = request.getFacebookToken();
		List<String> permissions = new ArrayList<>();
		URL url = new URL(FACEBOOK_PERMISSIONS_URL + facebookToken);
		URLConnection connection = url.openConnection();
		InputStream stream;
		stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		PuObject puObject = PuObject.fromJSON(IOUtils.toString(stream, endCoding));
		if (puObject == null) {
			return permissions;
		}
		if (puObject.variableExists("error")) {
			getLogger().debug("have error when query to facebook");
			return permissions;
		}
		String facebookId = puObject.getString("id");
		permissions.add(facebookId);
		getLogger().debug("facebookId " + facebookId);
		PuObject pe = puObject.getPuObject("permissions");
		PuArray array = pe.getPuArray("data");
		for (int i = 0; i < array.size(); i++) {
			PuObject data = array.getPuObject(i);
			String permission = data.getString("permission");
			permissions.add(permission);
		}

		return permissions;
	}

	private List<String> friendsInApp(String facebookToken) throws Exception {
		getLogger().debug("get facebook friends of {}", facebookToken);
		List<String> friends = new ArrayList<>();
		URL url = new URL(FACEBOOK_FRIENDS_URL + facebookToken + "&fields=installed");
		URLConnection connection = url.openConnection();
		InputStream stream;
		stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		PuObject puObject = PuObject.fromJSON(IOUtils.toString(stream, endCoding));
		if (puObject == null || puObject.variableExists("error")) {
			return friends;
		}
		PuArray array = puObject.getPuArray("data");
		for (int i = 0; i < array.size(); i++) {
			PuObject data = array.getPuObject(i);
			friends.add(data.getString("id"));
		}
		return friends;
	}

	/**
	 * make friend with facebook's friend send command
	 * {@link FriendCommand#MAKE_FRIEND} to {@link MakeFriendProcessor}
	 */
	private void makeFriendInApp(NotificationHelper notiHelper, String facebookId, String friendId,
			UserMongoBean userBean, String sessionId) {
		getLogger().debug("make friend between {} && {} ", facebookId, friendId);
		User friend = this.getUserManager().getUserByUserName(friendId);
		if (friend == null) {
			getLogger().debug("user {} null , don't make friend", friendId);
			return;
		}
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, FriendCommand.MAKE_FRIEND.getCode());
		data.setString(FriendField.SENDER_NAME, facebookId);
		data.setString(FriendField.RECEIVER_NAME, friendId);
		data.setInteger(FriendField.STATUS, 0);

		PuElement puElement = this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
		PuObject result = (PuObject) puElement;
		int status = result.getInteger(FriendField.STATUS);
		if (status == 0) {
			getLogger().debug("make friend success");
			pushNotificationToFriend(notiHelper, friendId, userBean);
			sendMakeFriendInAppLog(facebookId, friendId, sessionId);
		}
	}

	private void sendMakeFriendInAppLog(String userName, String friend, String sessionId) {
		String content = "user " + userName + " make friend when login with user " + friend;
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.MAKE_FRIEND_WHEN_LOGIN, userName, sessionId);
	}

	private void makeFriendWithFacebookFriends(String facebookId, String sessionId) throws Exception {
		getLogger().debug("make friend with facebook Friends of {}", facebookId);
		UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(facebookId);
		if (userBean == null) {
			getLogger().debug("error userBean null");
			return;
		}
		NotificationHelper notiHelper = new NotificationHelper(getContext());
		List<String> friends = friendsInApp(userBean.getFacebookToken());
		if (friends.size() <= 0) {
			getLogger().debug("have not any facebook friends ");
			return;
		}
		getLogger().debug("receive {} facebook friends", friends.size());
		for (String friend : friends) {
			makeFriendInApp(notiHelper, userBean.getUserName(), friend, userBean, sessionId);
		}
	}

	private void pushNotificationToFriend(NotificationHelper notiHelper, String friendId, UserMongoBean userBean) {
		getLogger().debug("send notification to friend {}", friendId);
		User receiver = this.getUserManager().getUserByUserName(friendId);
		String message = getMessagePushNotification(PushNotificationType.MAKE_FRIEND, userBean, friendId);
		if (receiver == null || message == null) {
			getLogger().debug("receiver null");
			return;
		}
		if (receiver.getSessions().size() > 0) {
			for (String sessionId : receiver.getSessions()) {
				getLogger().debug("send notification to {}", receiver.getUserName());
				if (this.getUserManager().isDeviceInApp(sessionId)) {
					getLogger().debug("send push api make friend");
					notiHelper.pushInApp(message, "makeFriend", receiver, PushNotificationType.MAKE_FRIEND);
				} else {
					getLogger().debug("send push notification by gcm");
					String deviceToken = this.getUserManager().getDeviceTokenBySessionId(sessionId);
					notiHelper.pushByGCM(message, receiver.getUserName(), deviceToken, "makeFriend");
				}
			}
		} else {
			notiHelper.pushByGCM(message, receiver.getUserName(), "", "makeFriend");
		}

	}

	private String getMessagePushNotification(PushNotificationType type, UserMongoBean bean, String friendId) {
		UserMongoBean friendBean = this.getUserMongoModel().findByFacebookId(friendId);
		if (friendBean == null) {
			return null;
		}
		PuObject message = new PuObject();
		message.setString(PNF.SENDER_NAME, bean.getUserName());
		message.setString(PNF.SENDER_DISPLAY_NAME, bean.getDisplayName());
		message.setString(PNF.AVATAR_SENDER, bean.getAvatar().getName());
		message.setString(PNF.RECEIVER_NAME, friendBean.getUserName());
		message.setString(PNF.RECEIVER_DISPLAY_NAME, friendBean.getDisplayName());
		message.setString(PNF.AVATAR_RECEIVER, friendBean.getAvatar().getName());
		message.setInteger(PNF.PUSH_NOTIFICATION_TYPE, type.getCode());

		return message.toJSON();
	}

	private void whenUserLoginSucess(String facebookId, LoginRequest request, List<String> permissions) {
		getLogger().debug("when login success");
		User user = null;
		user = getUserManager().addUserIfNotExists(facebookId);
//		this.getUserManager().userOnline(facebookId);
		this.updateUserOnlineTime(facebookId);
		if (user != null) {
			user.addSession(request.getSessionId());
			user.setStatus(StatusUser.ONLINE);
		}
		this.getUserManager().addNewUser(user);
		this.getUserManager().updateStatusUser(facebookId, StatusUser.ONLINE);
		this.getContext().getReporter().changeWhenUserOnline(facebookId);
		try {
			makeFriendWithFacebookFriends(facebookId, request.getSessionId());
		} catch (Exception e) {
			getLogger().debug("error when make friend from facebookFriends");
			e.printStackTrace();
		}
		makeFriendWithNhoBot(facebookId);
		
	}

	private void updateUserOnlineTime(String facebookId) {
		this.getUserMongoModel().updateUserOnlineTime(facebookId, System.currentTimeMillis());
	}

	private void sendLog(LoginRequest request, User user) {
		getLogger().debug("send login activity log to Nho UAMS");
		String content = "user " + user.getUserName() + " register";
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.REGISTER, user.getUserName(), request.getSessionId());
	}

	/**
	 * make friend with nho boot send command
	 * {@link FriendCommand#MAKE_FRIEND_WITH_BOT} to
	 * {@link MakeFriendWithBotProcessor}
	 */
	private void makeFriendWithNhoBot(String userName) {
		getLogger().debug("make user {} friend with bot", userName);
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, FriendCommand.MAKE_FRIEND_WITH_BOT.getCode());
		data.setString(FriendField.SENDER_NAME, userName);
		data.setString(FriendField.RECEIVER_NAME, BotNho.USER_NAME);
		PuElement puElement = this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
		PuObject result = (PuObject) puElement;
		int status = result.getInteger(FriendField.STATUS);
		if (status == 0) {
			getLogger().debug("make friend with bot of {} sucessful", userName);
		} else {
			getLogger().debug("make friend with bot have something wrong");
		}
	}

}
