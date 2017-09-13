package com.nho.server.processors.channel;

import java.util.Set;

import com.nho.message.MessageType;
import com.nho.message.request.channel.SoundPermissionMessage;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;

@NhoCommandProcessor(command = { MessageType.SOUND_PERMISSION })
public class PermissionProcessor extends AbstractNhoProcessor<SoundPermissionMessage> {

	@Override
	protected void process(SoundPermissionMessage request) throws Exception {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		ChannelHelper helper = new ChannelHelper(getContext());
		if (user == null || helper.isChannelChatWithBot(request.getChannelId())) {
			getLogger().debug("user not login");
			return;
		}
		if (helper.isRecieverOnline(request.getChannelId(), request.getFrom())) {
			boolean isReceiverBuzy = helper.isReceiverIsBusy(request.getChannelId(), request.getFrom());
			if (isReceiverBuzy) {
				getLogger().debug("receiver is busy");
				return;
			}
			Set<String> userInChannels = helper.getUsersInChannelByChannelId(request.getChannelId(), request.getFrom());
			this.sendToUserNames(request, userInChannels);
		} else {
			getLogger().debug("receiver is offline");
		}
	}
}
