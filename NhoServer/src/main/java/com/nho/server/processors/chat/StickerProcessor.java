package com.nho.server.processors.chat;

import java.util.Set;

import com.nho.message.MessageType;
import com.nho.message.request.chat.StickerChatMessage;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChatException;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.BotNho;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.STICKER_CHAT })
public class StickerProcessor extends AbstractNhoProcessor<StickerChatMessage> {

	@Override
	protected void process(StickerChatMessage request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("user not login");
				return;
			}
			ChannelHelper helper = new ChannelHelper(getContext());
			if (helper.isChannelChatWithBot(request.getChannelId())) {
				getLogger().debug("user {} send sticker {} to bot ", user.getUserName(), request.getStickerType());
				chatWithBot(request);
			} else {
				Set<String> userInChannel = helper.getUsersInChannelByChannelId(request.getChannelId(),
						user.getUserName());
				this.sendToUserNames(request, userInChannel);
				logSendSticker(request, user);
			}
		} catch (Exception exception) {
			throw new ChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	private void logSendSticker(StickerChatMessage message, User user) {
		String content = "user " + user.getUserName() + " send sticker " + message.getStickerType();
		LoggingHelper helper = new LoggingHelper(getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.SEND_STICKER, user.getUserName(), message.getSessionId());
	}

	/**
	 * send sticker chat to user by boot
	 */
	private void chatWithBot(StickerChatMessage request) {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		String stickerType = request.getStickerType();
		int value_type = 0;
		try {
			value_type = Integer.valueOf(stickerType) % 10;
		} catch (Exception exception) {
			getLogger().debug("invalid stickertype : " + stickerType);
		}
		switch (value_type) {
		case 1:
			request.setStickerType(BotNho.STICKER_CHAO);
			break;
		case 2:
			request.setStickerType(BotNho.STICKER_DUNG);
			break;
		case 3:
			request.setStickerType(BotNho.STICKER_SAI);
			break;
		}
		getLogger().debug("bot send sticker {} to user {}", request.getStickerType(), user.getUserName());
		request.setSentTime(System.currentTimeMillis());
		try {
			Thread.sleep(2000);
			getLogger().debug("sleep 2s before send response to client ");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.send(request, user.getSessions());
	}
}
