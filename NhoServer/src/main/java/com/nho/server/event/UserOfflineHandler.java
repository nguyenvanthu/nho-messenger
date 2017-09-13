package com.nho.server.event;

import com.nhb.common.data.PuObject;
import com.nhb.eventdriven.Event;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.server.NhoServer;
import com.nho.server.entity.user.UserEvent;
import com.nho.server.statics.HandlerCollection;

public class UserOfflineHandler extends NhoEventHandler {
	public UserOfflineHandler(NhoServer context) {
		super.setContext(context);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		UserEvent userEvent = (UserEvent) event;
		String sessionId = userEvent.getSessionId();
		this.getContext().closeSession(sessionId);
		String userName = userEvent.getUserName();
		if(this.getContext().getUserManager().isUserOnline(userName)){
			getLogger().debug("user reconnect successfull");
			return;
		}
		getLogger().debug("user {} in session {} disconnect",userName, sessionId);
		this.getUserMongoModel().updateUserOnlineTime(userName, System.currentTimeMillis());
//		this.getContext().getUserManager().addOfflineUserWithTime(userName);
		this.getContext().getReporter().cleanUserInfo(sessionId);
		this.getContext().getReporter().changeWhenUserOffline(userName);
		// remove live object is blocked by user
		PuObject removeLiveObjer = new PuObject();
		removeLiveObjer.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_OBJ_BY_USER.getCode());
		removeLiveObjer.setString(ChatField.USER_NAME, userName);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, removeLiveObjer);
		// remove data message user chat with boot
		PuObject removeUserChatBot = new PuObject();
		removeUserChatBot.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_DATA_MSG_BOT.getCode());
		removeUserChatBot.setString(ChatField.USER_NAME, userName);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, removeUserChatBot);
	}
}
