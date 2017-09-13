package com.nho.server.task.impl;

import java.util.Set;

import com.nhb.common.data.PuObject;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.request.chat.StickerChatMessage;
import com.nho.server.NhoServer;
import com.nho.server.entity.user.User;
import com.nho.server.statics.BotNho;
import com.nho.server.statics.HandlerCollection;
import com.nho.server.task.AbstractTask;
import com.nho.server.task.Timer;
import com.nho.statics.ChatMode;

public class SendTutorialChatTask extends AbstractTask {
	public SendTutorialChatTask(NhoServer context) {
		super.setContext(context);
	}

	@Override
	public void run() {
		try {
			Set<String> userChatWithBots = this.getContext().getUserManager().getUserChatWithBots();
			if (userChatWithBots.size() > 0) {
				for (String userName : userChatWithBots) {
					PuObject getWaitTimer = new PuObject();
					getWaitTimer.setInteger(ChatField.COMMAND, ChannelCommand.GET_WAIT_TIME.getCode());
					getWaitTimer.setString(ChatField.USER_NAME, userName);
					PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER,
							getWaitTimer);
					int status = result.getInteger(ChatField.STATUS);
					long waitTime = 0;
					if (status == 0) {
						waitTime = result.getLong(ChatField.WAIT_TIME);
					}
					if (waitTime >= Timer.WAIT_TIME) {
						User user = this.getContext().getUserManager().getUserByUserName(userName);
						if (user != null) {
							sendTutorialToUser(user);
						}
					}
				}
			}
		} catch (Exception exception) {
			getLogger().debug("exception when send tutorial chat: " + exception);
		}
	}

	private void sendTutorialToUser(User user) {
		int numberInteract = 0;
		StickerChatMessage message = new StickerChatMessage();
		message.setChannelId("bot");
		message.setSentTime(System.currentTimeMillis());
		message.setData(new PuObject());
		ChatMode mode = this.getContext().getUserManager().getModeChatOfUser(user.getUserName());
		if (mode != null) {
			switch (mode) {
			case DEFAULT:
				numberInteract = BotNho.incrementInteraction(user.getUserName());
				getLogger().debug("send tutorial {} to user {}", numberInteract % 4, user.getUserName());
				message = getStickerForDefaultChat(message, numberInteract);
				break;
			case LIVECHAT:
				numberInteract = BotNho.incrementInteractVgdn(user.getUserName());
				getLogger().debug("send tutorial {} to user {}", numberInteract % 3, user.getUserName());
				message = getStickerForLiveChat(message, numberInteract);
				break;
			}
		}

		this.getContext().send(message, user.getSessions());
		this.resetWaitTimeOfUserWithBot(user.getUserName());
	}

	private StickerChatMessage getStickerForDefaultChat(StickerChatMessage message, int numberInteract) {
		int interact = numberInteract % 4;
		switch (interact) {
		case 1:
			message.setStickerType("step1");
			break;
		case 2:
			message.setStickerType("step2");
			break;
		case 3:
			message.setStickerType("step3");
			break;
		case 0:
			message.setStickerType("step4");
			break;
		default:
			break;
		}
		return message;
	}

	private StickerChatMessage getStickerForLiveChat(StickerChatMessage message, int numberInteract) {
		int interact = numberInteract % 3;
		switch (interact) {
		case 1:
			message.setStickerType("vgdn_step1");
			break;
		case 2:
			message.setStickerType("vgdn_step2");
			break;
		case 0:
			message.setStickerType("vgdn_step3");
			break;
		default:
			break;
		}
		return message;
	}

	private void resetWaitTimeOfUserWithBot(String userName) {
		getLogger().debug("reset waited time of user {}  with bot ", userName);
		PuObject resetWaiter = new PuObject();
		resetWaiter.setInteger(ChatField.COMMAND, ChannelCommand.RESET_WAIT_TIME.getCode());
		resetWaiter.setString(ChatField.USER_NAME, userName);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, resetWaiter);
	}

}
