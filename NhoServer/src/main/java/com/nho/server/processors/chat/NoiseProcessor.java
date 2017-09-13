package com.nho.server.processors.chat;

import java.util.Set;

import com.nho.message.MessageType;
import com.nho.message.request.chat.NoiseLevelRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChatException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;

@NhoCommandProcessor(command = { MessageType.NOISE_LEVEL })
public class NoiseProcessor extends AbstractNhoProcessor<NoiseLevelRequest> {

	@Override
	protected void process(NoiseLevelRequest request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null || request.getChannelId() == null) {
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			Set<String> subcribers = helper.getUsersInChannelByChannelId(request.getChannelId(), user.getUserName());
			boolean isReceiverBuzy = helper.isReceiverIsBusy(request.getChannelId(), user.getUserName());
			if (!isReceiverBuzy) {
				this.sendToUserNames(request, subcribers);
			}
		} catch (Exception exception) {
			throw new ChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}
}