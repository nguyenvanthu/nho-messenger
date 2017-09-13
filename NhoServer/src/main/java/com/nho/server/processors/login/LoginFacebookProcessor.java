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
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.login.LoginFacebookRequest;
import com.nho.message.response.login.LoginFacebookResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.avatar.Avatar;
import com.nho.server.entity.user.User;
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

@NhoCommandProcessor(command = { MessageType.LOGIN_WITH_FACEBOOK })
public class LoginFacebookProcessor extends AbstractNhoProcessor<LoginFacebookRequest> {
	private static final String FACEBOOK_URL = "https://graph.facebook.com/me?access_token=";
	private static final String FACEBOOK_FRIENDS_URL = "https://graph.facebook.com/v2.7/me/friends?access_token=";
	private static final String FACEBOOK_AVATAR_URL = "https://graph.facebook.com/me?fields=picture.height(200).width(200)&access_token=";

	@Override
	protected void process(LoginFacebookRequest request) throws Exception {
		LoginFacebookResponse response = new LoginFacebookResponse();
		String facebookId = request.getFacebookId();
		String facebookToken = request.getFacebookToken();
		if (facebookId == null || facebookToken == null) {
			getLogger().debug("facebookId null || facebookToken null");
			response.setSuccess(false);
			response.setError(Error.FACE_ID_NOT_FOUND);
			this.send(response, request.getSessionId());
			return;
		}

		String faceId = getFacebookId(facebookToken);
		if (faceId == null || !faceId.equals(facebookId)) {
			getLogger().debug("facebookToken expired ");
			response.setSuccess(false);
			response.setError(Error.FACE_TOKEN_EXPIRED);
			this.send(response, request.getSessionId());
			return;
		}
		UserMongoBean bean = findByFacebookId(facebookId);
		if (bean != null) {
			getLogger().debug("login sucess");
			if (!bean.getFacebookToken().equals(request.getFacebookToken())) {
				getLogger().debug("update facebookToken");
				this.getUserMongoModel().updateFacebookToken(facebookId, request.getFacebookToken());
			}
			String newAvtUrl = getUserAvtUrl(facebookToken);
			if (newAvtUrl != null) {
				if (!bean.getAvatar().getUrl().equals(newAvtUrl)) {
					getLogger().debug("update user avatar ");
					Avatar newAvt = new Avatar(AvatarType.IMAGE_FACEBOOK.getCode(), newAvtUrl, newAvtUrl);
					this.getUserMongoModel().updateAvatar(faceId, newAvt);
				}
			}
			getLogger().debug("displayname: " + bean.getDisplayName());
			whenUserLoginSucess(request);
			response.setSuccess(true);
			response.setAvatarName(bean.getAvatar().getName());
			response.setDisplayName(bean.getDisplayName());
			response.setToken(bean.getFacebookToken());
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user != null) {
				getLogger().debug("user added to user manager ");
			}
			this.send(response, request.getSessionId());
			this.sendLog(request, user);
			return;
		}
		response.setSuccess(false);
		response.setError(Error.FACE_ID_NOT_FOUND);
		this.send(response, request.getSessionId());
	}

	private String getFacebookId(String facebookToken) throws Exception {
		String facebookId = null;
		URL url = new URL(FACEBOOK_URL + facebookToken);
		URLConnection connection = url.openConnection();
		InputStream stream;
		stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		PuObject puObject = PuObject.fromJSON(IOUtils.toString(stream, endCoding));
		if (puObject == null) {
			return null;
		}
		if (puObject.variableExists("error")) {
			return null;
		}
		facebookId = puObject.getString("id");
		return facebookId;
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

	private UserMongoBean findByFacebookId(String facebookId) {
		return this.getUserMongoModel().findByFacebookId(facebookId);
	}

	private void whenUserLoginSucess(LoginFacebookRequest request) {
		getLogger().debug("when login sucess");
		User user = null;
		String facebookId = request.getFacebookId();
		user = getUserManager().addUserIfNotExists(facebookId);
//		this.getUserManager().userOnline(facebookId);
		if (user != null) {
			user.addSession(request.getSessionId());
			user.setStatus(StatusUser.ONLINE);
		}
		this.getUserManager().addNewUser(user);
		this.getUserManager().updateStatusUser(facebookId, StatusUser.ONLINE);
		this.updateUserOnlineTime(facebookId);
		this.getContext().getReporter().changeWhenUserOnline(facebookId);
		try {
			makeFriendWithFacebookFriends(request.getFacebookId(), request.getFacebookToken(), request.getSessionId());
		} catch (Exception e) {
			getLogger().debug("error when make friend with facebook friends " + e);
			e.printStackTrace();
		}
		makeFriendWithNhoBot(facebookId);
		
	}

	private void updateUserOnlineTime(String facebookId) {
		this.getUserMongoModel().updateUserOnlineTime(facebookId, System.currentTimeMillis());
	}

	private void sendLog(LoginFacebookRequest request, User user) {
		getLogger().debug("send login activity log to Nho UAMS");
		String content = "user " + user.getUserName() + " login in session " + request.getSessionId();
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.LOGIN, user.getUserName(), request.getSessionId());
	}

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

	private List<String> friendsInApp(String facebookToken) throws Exception {
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
			UserMongoBean userBean,String sessionId) {
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
			sendMakeFriendInAppLog(facebookId, friendId,sessionId);
		}
	}

	private void sendMakeFriendInAppLog(String userName, String friend,String sessionId) {
		String content = "user " + userName + " make friend when login with user " + friend;
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.MAKE_FRIEND_WHEN_LOGIN, userName, sessionId);
	}

	private void makeFriendWithFacebookFriends(String facebookId, String facebookToken, String sessionId)
			throws Exception {
		getLogger().debug("make friend with facebook Friends {}", facebookId);
		UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(facebookId);
		if (userBean == null) {
			getLogger().debug("userBean null");
			return;
		}
		NotificationHelper notiHelper = new NotificationHelper(getContext());
		List<String> friends = friendsInApp(facebookToken);
		if (friends.size() <= 0) {
			getLogger().debug("have not facebook friends ");
			return;
		}
		getLogger().debug("receive {} facebook friends", friends.size());
		for (String friend : friends) {
			makeFriendInApp(notiHelper, userBean.getUserName(), friend, userBean,sessionId);
		}
	}

	private void pushNotificationToFriend(NotificationHelper notiHelper, String friendId, UserMongoBean userBean) {
		User receiver = this.getUserManager().getUserByUserName(friendId);
		String message = getMessagePushNotification(PushNotificationType.MAKE_FRIEND, userBean, friendId);
		if (receiver == null || message == null) {
			return;
		}
		if (receiver.getSessions().size() > 0) {
			for (String sessionId : receiver.getSessions()) {
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

}
