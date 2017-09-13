package com.nho.message;

import java.util.concurrent.atomic.AtomicInteger;

import com.nho.message.request.PingRequest;
import com.nho.message.request.channel.ChangeThemeColorRequest;
import com.nho.message.request.channel.CreateChannelRequest;
import com.nho.message.request.channel.CreateChannelWithBotRequest;
import com.nho.message.request.channel.JoinChannelRequest;
import com.nho.message.request.channel.LeaveChannelRequest;
import com.nho.message.request.channel.PokeRequest;
import com.nho.message.request.channel.SoundPermissionMessage;
import com.nho.message.request.channel.StateAppChangeRequest;
import com.nho.message.request.chat.BubbleChatMessage;
import com.nho.message.request.chat.ChangeModeChatWithBotRequest;
import com.nho.message.request.chat.ChatMessage;
import com.nho.message.request.chat.GetMessageOfflines;
import com.nho.message.request.chat.NoiseLevelRequest;
import com.nho.message.request.chat.RecognizeRequest;
import com.nho.message.request.chat.StickerChatMessage;
import com.nho.message.request.chat.anything.CanMoveLiveObjectRequest;
import com.nho.message.request.chat.anything.DeleteLiveObjectMessage;
import com.nho.message.request.chat.anything.DrawLiveObjectMessage;
import com.nho.message.request.chat.anything.GetListObjectInChannelRequest;
import com.nho.message.request.chat.anything.MakeLiveObject;
import com.nho.message.request.chat.anything.MoveLiveObjectMessage;
import com.nho.message.request.chat.anything.PlayGameRequest;
import com.nho.message.request.chat.anything.ReleaseLiveObjectRequest;
import com.nho.message.request.feedback.SendFeedback;
import com.nho.message.request.friend.AcceptFriendRequest;
import com.nho.message.request.friend.BlockFriendRequest;
import com.nho.message.request.friend.CancelFriendRequest;
import com.nho.message.request.friend.GetListFriend;
import com.nho.message.request.friend.GetListFriendPendding;
import com.nho.message.request.friend.GetListInviteFriend;
import com.nho.message.request.friend.GetListStatusFriend;
import com.nho.message.request.friend.IgnoreFriendRequest;
import com.nho.message.request.friend.SearchFriendRequest;
import com.nho.message.request.friend.SendFriendRequest;
import com.nho.message.request.friend.UnBlockFriendRequest;
import com.nho.message.request.login.DeleteUserRequest;
import com.nho.message.request.login.FakeLoginRequest;
import com.nho.message.request.login.LoginFacebookRequest;
import com.nho.message.request.login.LoginRequest;
import com.nho.message.request.login.LogoutRequest;
import com.nho.message.request.login.ReturnAppRequest;
import com.nho.message.request.login.UpdateFacebookToken;
import com.nho.message.request.login.UpdateProfile;
import com.nho.message.request.notification.CreateApplicationRequest;
import com.nho.message.request.notification.RegisterPushNotificationRequest;
import com.nho.message.request.notification.TestPushNotification;
import com.nho.message.response.ErrorEvent;
import com.nho.message.response.GetMessageOfflineResponses;
import com.nho.message.response.channel.ChangeThemeColorResponse;
import com.nho.message.response.channel.ChatInvitationResponse;
import com.nho.message.response.channel.ChatInvitationWithBotResponse;
import com.nho.message.response.channel.InvitedToChatEvent;
import com.nho.message.response.channel.JoinChannelResponse;
import com.nho.message.response.channel.JoinedToChannelResponse;
import com.nho.message.response.channel.LeaveChannelResponse;
import com.nho.message.response.channel.PokeReponse;
import com.nho.message.response.channel.StateAppChangeResponse;
import com.nho.message.response.chat.anything.CanMoveLiveObjectResponse;
import com.nho.message.response.chat.anything.GetLiveObjectsInChannelResponse;
import com.nho.message.response.chat.anything.ListObjectInChannelResponse;
import com.nho.message.response.chat.anything.MakeObjectCompleteResponse;
import com.nho.message.response.chat.anything.ReleaseObjResponse;
import com.nho.message.response.connection.ConnectionResponse;
import com.nho.message.response.connection.DisconnectEvent;
import com.nho.message.response.connection.PongEvent;
import com.nho.message.response.friend.AcceptFriendResponse;
import com.nho.message.response.friend.CancelFriendResponse;
import com.nho.message.response.friend.GetListFriendPenddingResponse;
import com.nho.message.response.friend.GetListFriendResponse;
import com.nho.message.response.friend.GetListInviteFriendResponse;
import com.nho.message.response.friend.IgnoreFriendResponse;
import com.nho.message.response.friend.ListStateFriendResponse;
import com.nho.message.response.friend.SearchFriendResponse;
import com.nho.message.response.friend.SendFriendResponse;
import com.nho.message.response.friend.StateFriendChangeResponse;
import com.nho.message.response.friend.UnBlockFriendResponse;
import com.nho.message.response.login.FakeLoginResponse;
import com.nho.message.response.login.LoginFacebookResponse;
import com.nho.message.response.login.LoginResponse;
import com.nho.message.response.login.LogoutResponse;
import com.nho.message.response.login.ReturnAppResponse;
import com.nho.message.response.login.UpdateProfileFriendResponse;
import com.nho.message.response.login.UpdateProfileResponse;
import com.nho.message.response.notification.CreateApplicationResponse;
import com.nho.message.response.notification.PushNotificationResponse;
import com.nho.message.response.notification.RegisterPushNotificationResponse;
import com.nho.statics.BlockFriendResponse;

public enum MessageType {

	ERROR(ErrorEvent.class),

	CONNECTION_RESPONSE(ConnectionResponse.class),
	DISCONNECT_EVENT(DisconnectEvent.class),

	LOGIN(LoginRequest.class),
	LOGIN_RESPONSE(LoginResponse.class),

	LOGOUT(LogoutRequest.class),
	LOGOUT_RESPONSE(LogoutResponse.class),

	CHAT(ChatMessage.class),

	CREATE_CHANNEL(CreateChannelRequest.class),
	JOIN_CHANNEL(JoinChannelRequest.class),

	PING(PingRequest.class),
	PONG(PongEvent.class),

//	CHANNEL_USER_UPDATE(ChannelUserUpdateEvent.class),

	LEAVE_CHANNEL(LeaveChannelRequest.class),
	LEAVE_CHANNEL_RESPONSE(LeaveChannelResponse.class),

	INVITED_TO_CHAT_EVENT(InvitedToChatEvent.class),
	CHAT_INVITATION_RESPONSE(ChatInvitationResponse.class), 
	
	SEND_FRIEND_REQUEST(SendFriendRequest.class),
	SEND_FRIEND_RESPONSE(SendFriendResponse.class),
	
	GET_LIST_FRIEND_PENDDING(GetListFriendPendding.class),
	GET_LIST_FRIEND_PENDDING_RESPONSE(GetListFriendPenddingResponse.class),
	
	ACCEPT_FRIEND_REQUEST(AcceptFriendRequest.class),
	ACCEPT_FRIEND_RESPONSE(AcceptFriendResponse.class), 
	
	IGNORE_FRIEND_REQUEST(IgnoreFriendRequest.class),
	IGNORE_FRIEND_RESPONSE(IgnoreFriendResponse.class), 
	
	BLOCK_FRIEND(BlockFriendRequest.class),
	BLOCK_FRIEND_RESPONSE(BlockFriendResponse.class), 
	
	CANCEL_FRIEND(CancelFriendRequest.class),
	CANCEL_FRIEND_RESPONSE(CancelFriendResponse.class),
	
	GET_LIST_INVITE_FRIEND(GetListInviteFriend.class),
	GET_LIST_INVITE_FRIEND_RESPONSE(GetListInviteFriendResponse.class), 
	
	UNBLOCK_FRIEND(UnBlockFriendRequest.class),
	UNBLOCK_FRIEND_RESPONSE(UnBlockFriendResponse.class),

	GET_MESSAGE_OFFLINE(GetMessageOfflines.class),
	GET_MESSAGE_OFFLINE_RESPONSE(GetMessageOfflineResponses.class),
	
	SEARCH_FRIEND(SearchFriendRequest.class),
	SEARCH_FRIEND_RESPONSE(SearchFriendResponse.class),
	
	CREATE_APPLICATION(CreateApplicationRequest.class),
	CREATE_APPLICATION_RESPONSE(CreateApplicationResponse.class),
	
	REGISTER_PUSH_NOTIFICATION(RegisterPushNotificationRequest.class),
	REGISTER_PUSH_NOTIFICATION_RESPONSE(RegisterPushNotificationResponse.class),
	
	GET_LIST_FRIEND(GetListFriend.class),
	GET_LIST_FRIEND_RESPONSE(GetListFriendResponse.class),
	
	POKE_REQUEST(PokeRequest.class),
	POKE_RESPONSE(PokeReponse.class),
	
	TEST_PUSH(TestPushNotification.class), 
	STATE_APP_CHANGE(StateAppChangeRequest.class),
	STATE_FRIEND_CHANGE(StateFriendChangeResponse.class),
	
	LIST_STATE_FRIEND_RESPONSE(ListStateFriendResponse.class),
	GET_LIST_STATUS_FRIEND(GetListStatusFriend.class), 
	
	PUSH_NOTIFICATION_RESPONSE(PushNotificationResponse.class),
	STATE_APP_CHANGE_RESPONSE(StateAppChangeResponse.class), 
	
	NOISE_LEVEL(NoiseLevelRequest.class), 
	
	CREATE_CHANNEL_WITH_BOT(CreateChannelWithBotRequest.class), 
	CHAT_INVITATION_WITH_BOT_RESPONSE(ChatInvitationWithBotResponse.class), 
	
	STICKER_CHAT(StickerChatMessage.class), 
	DRAW_LIVE_OBJECT(DrawLiveObjectMessage.class), 
	PICK_DATA_MESSAGE(MoveLiveObjectMessage.class), 
	
	MAKE_OBJECT_CHAT(MakeLiveObject.class), 
	MAKE_OBJECT_CHAT_RESPONSE(MakeObjectCompleteResponse.class), 
	
	CAN_MOVE_DATA_REQUEST(CanMoveLiveObjectRequest.class), 
	DELETE_LIVE_OBJECT(DeleteLiveObjectMessage.class), 
	
	GET_LIST_OBJECT_IN_CHANNEL(GetListObjectInChannelRequest.class), 
	GET_LIST_OBJECT_IN_CHANNEL_RESPONSE(ListObjectInChannelResponse.class), 
	RELEASE_OBJ(ReleaseLiveObjectRequest.class), 
	RELEASE_OBJ_RESPONSE(ReleaseObjResponse.class),
	CAN_MOVE_LIVE_OBJ_RESPONSE(CanMoveLiveObjectResponse.class), 
	CHANGE_MODE_CHAT(ChangeModeChatWithBotRequest.class), 
	PLAY_GAME(PlayGameRequest.class), 
	LOGIN_WITH_FACEBOOK(LoginFacebookRequest.class), 
	LOGIN_WITH_FACEBOOK_RESPONSE(LoginFacebookResponse.class), 
	UPDATE_TOKEN(UpdateFacebookToken.class),
	FEEDBACK_MESSAGE(SendFeedback.class), 
	SOUND_PERMISSION(SoundPermissionMessage.class), 
	CHANGE_THEME_COLOR(ChangeThemeColorRequest.class), 
	CHANGE_THEME_COLOR_RESPONSE(ChangeThemeColorResponse.class), 
	UPDATE_PROFILE(UpdateProfile.class), 
	UPDATE_PROFILE_FRIEND_RESPONSE(UpdateProfileFriendResponse.class), 
	UPDATE_PROFILE_RESPONSE(UpdateProfileResponse.class), 
	GET_LIVE_OBJECTS_IN_CHANNEL(GetLiveObjectsInChannelResponse.class),
	DELETE_USER(DeleteUserRequest.class), 
	RECOGNIZE_REQUEST(RecognizeRequest.class), 
	JOINED_TO_CHANNEL_RESPONSE(JoinedToChannelResponse.class), 
	JOIN_CHANNEL_RESPONSE(JoinChannelResponse.class), 
	RETURN_APP(ReturnAppRequest.class),
	RETURN_APP_RESPONSE(ReturnAppResponse.class),
	BUBBLE_CHAT(BubbleChatMessage.class), 
	FAKE_LOGIN(FakeLoginRequest.class), 
	FAKE_LOGIN_RESPONSE(FakeLoginResponse.class);
	
	
	private static AtomicInteger idSeed = null;

	private static final int genId() {
		if (idSeed == null) {
			idSeed = new AtomicInteger(0);
		}
		return idSeed.incrementAndGet();
	}

	private final int id = genId();
	private final Class<? extends NhoMessage> clazz;

	private MessageType(Class<? extends NhoMessage> clazz) {
		this.clazz = clazz;
	}

	final Class<? extends NhoMessage> getMessageClass() {
		return this.clazz;
	}

	public int getId() {
		return this.id;
	}

	public static final MessageType fromId(int id) {
		for (MessageType type : values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}
}
