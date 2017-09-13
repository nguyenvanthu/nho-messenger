package com.nho.server.processors.chat;

import java.util.Set;

import com.nho.message.MessageType;
import com.nho.message.request.chat.BubbleChatMessage;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;

@NhoCommandProcessor(command = { MessageType.BUBBLE_CHAT })
public class BubbleChatProcessor extends AbstractNhoProcessor<BubbleChatMessage>{

	@Override
	protected void process(BubbleChatMessage request) throws Exception {
		String sessionId = request.getSessionId();
		User user = getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null || request.getTo() == null) {
			getLogger().debug("error user null");
			return;
		}
		ChannelHelper helper = new ChannelHelper(getContext());
		if (helper.isChannelChatWithBot(request.getTo())) {
			copyFromBot(request, user);
			return;
		}
		Set<String> userInChannelOnlines = helper.getOnlineUsersInChannel(request.getTo(), request.getFrom());
		getLogger().debug("number userOnlines in channel "+userInChannelOnlines.size());
		if (userInChannelOnlines.size() > 0) {
			boolean isReceiverBuzy = helper.isReceiverIsBusy(request.getTo(), request.getFrom());
			getLogger().debug("receiver is buzy:  " + isReceiverBuzy);
			if (!isReceiverBuzy) {
				getLogger().debug("start send chat message id " + request.getMessageId());
				request.setFrom(user.getUserName());
				getLogger().debug("send messsage chat to receiver ");
				for (String u : userInChannelOnlines) {
					User userOnline = this.getUserManager().getUserByUserName(u);
					this.send(request, userOnline.getSessions());
				}
				getLogger().debug("send message chat done , channelId: " + request.getTo());
			} else {
				getLogger().debug("cannot send chat message because reciver is buzy");
				request.setError(Error.RECEIVER_BUZY);
				this.send(request, sessionId);
			}
		} else {
			getLogger().debug("error user receiver chat message offline ");
			request.setError(Error.USER_OFFLINE);
			this.send(request, sessionId);
		}
	}
	private void copyFromBot(BubbleChatMessage request, User user){
		
	}
}
