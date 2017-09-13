package com.nho.server.processors.chat.anything;

import java.util.Set;

import com.nho.message.MessageType;
import com.nho.message.request.chat.anything.PlayGameRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.LiveChatException;
import com.nho.server.exception.UserNotLoggedInException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.PLAY_GAME })
public class PlayGameProcessor extends AbstractNhoProcessor<PlayGameRequest> {

	@Override
	protected void process(PlayGameRequest request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("user not loggin");
				throw new UserNotLoggedInException();
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				return;
			}
			Set<String> subs = helper.getUsersInChannelByChannelId(request.getChannelId(), user.getUserName());
			this.sendToUserNames(request, subs);
			sendLogPlayGame(user.getUserName(),request.getSessionId());
		} catch (Exception exception) {
			throw new LiveChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	private void sendLogPlayGame(String userName,String sessionId) {
		String content = "user " + userName + " play game";
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.PLAY_GAME, userName, sessionId);
	}
}
