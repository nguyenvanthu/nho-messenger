package com.nho.server.processors.channel;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.chat.router.impl.ResetWaitTimeOfUserProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.channel.CreateChannelWithBotRequest;
import com.nho.message.request.chat.StickerChatMessage;
import com.nho.message.response.channel.ChatInvitationWithBotResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChannelException;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.BotNho;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.ChatMode;
import com.nho.statics.Error;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.CREATE_CHANNEL_WITH_BOT })
public class CreateChannelWithBotProcessor extends AbstractNhoProcessor<CreateChannelWithBotRequest> {

	@Override
	protected void process(CreateChannelWithBotRequest request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user not login in CreateChannelWithBotProcessor");
				ChatInvitationWithBotResponse chatInvitationResponse = new ChatInvitationWithBotResponse();
				chatInvitationResponse.setSuccessful(false);
				chatInvitationResponse.setError(Error.USER_NOT_LOGGED_IN);
				this.send(chatInvitationResponse, request.getSessionId());
				return;
			}
			String channelId = createChannelWithBot(user.getUserName());
			ChatInvitationWithBotResponse chatInvitationResponse = new ChatInvitationWithBotResponse();
			chatInvitationResponse.setSuccessful(true);
			chatInvitationResponse.setChannelId(channelId);
			this.send(chatInvitationResponse, request.getSessionId());
			this.getUserManager().addUserChatWithBot(user.getUserName(), ChatMode.DEFAULT);
			BotNho.addInteraction(user.getUserName());
			Thread.sleep(3000);
			sendFirstTutorial(user);
			resetWaitTimeOfUser(user.getUserName());
			logChatWithBot(user.getUserName(), request.getSessionId());
		} catch (Exception exception) {
			throw new ChannelException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}

	/**
	 * create channel with boot send command
	 * {@link ChannelCommand#CREATE_CHANNEL_WITH_BOT} to
	 * {@link com.nho.chat.router.impl.CreateChannelWithBotProcessor}
	 * 
	 * @return channelId
	 */
	private String createChannelWithBot(String userName) {
		getLogger().debug("start create chat with bot ");
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.CREATE_CHANNEL_WITH_BOT.getCode());
		data.setString(ChatField.INVITER_USER, userName);
		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			result.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
			String channelId = result.getString(ChatField.CHANNEL_ID);
			getLogger().debug("result channelId from ChatServer is " + channelId);
			return channelId;
		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from ChannelServer ", e);
			return null;
		}

	}

	/**
	 * reset wait time of boot with user before boot interact again send command
	 * {@link ChannelCommand#REMOVE_WAIT_TIME} to
	 * {@link ResetWaitTimeOfUserProcessor}
	 */
	private void resetWaitTimeOfUser(String userName) {
		getLogger().debug("reset wait time of user " + userName);
		PuObject resetWaiter = new PuObject();
		resetWaiter.setInteger(ChatField.COMMAND, ChannelCommand.RESET_WAIT_TIME.getCode());
		resetWaiter.setString(ChatField.USER_NAME, userName);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, resetWaiter);
	}

	private void sendFirstTutorial(User user) {
		StickerChatMessage message = new StickerChatMessage();
		message.setChannelId("bot");
		message.setSentTime(System.currentTimeMillis());
		message.setData(new PuObject());
		message.setStickerType("step1");
		this.send(message, user.getSessions());
	}

	private void logChatWithBot(String userName, String sessionId) {
		String content = "user " + userName + " chat with bot";
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.CREATE_CHANNEL_WITH_BOT, userName, sessionId);
	}
}
