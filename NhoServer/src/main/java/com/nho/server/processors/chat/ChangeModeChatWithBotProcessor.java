package com.nho.server.processors.chat;

import com.nho.message.MessageType;
import com.nho.message.request.chat.ChangeModeChatWithBotRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChatException;
import com.nho.server.exception.UserNotLoggedInException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.ChatMode;

@NhoCommandProcessor(command = { MessageType.CHANGE_MODE_CHAT })
public class ChangeModeChatWithBotProcessor extends AbstractNhoProcessor<ChangeModeChatWithBotRequest> {

	@Override
	protected void process(ChangeModeChatWithBotRequest request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().equals("user not loggin ");
				throw new UserNotLoggedInException();
			}
			changeModeChatWithBot(user, request.getMode());
		} catch (Exception exception) {
			throw new ChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	private void changeModeChatWithBot(User user, ChatMode mode) {
		this.getUserManager().addUserChatWithBot(user.getUserName(), mode);
	}

}
