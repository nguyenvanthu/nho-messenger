package com.nho.chat.router.impl;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.data.ChannelMongoBean;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;

@ChatCommandProcessor(command = { ChannelCommand.CREATE_CHANNEL_WITH_BOT })
public class CreateChannelWithBotProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		getLogger().debug("receive createChannelWithBot ...");
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.INVITER_USER, PuDataType.STRING);
		String inviterUser = data.getString(ChatField.INVITER_USER);
		ChannelMongoBean channel = createChannelWithBot(inviterUser);
		response.setString(ChatField.CHANNEL_ID, channel.getId());
		getLogger().debug("channel Id with bot " + channel.getId());
		return response;
	}
	/**
	 * create channel and insert to database
	 * @return {@link ChannelMongoBean}
	 */
	private ChannelMongoBean createChannelWithBot(String userName) {

		List<String> users = new ArrayList<>();
		users.add(userName);
		users.add("bot");
		List<ChannelMongoBean> channelMongoBeans = new ArrayList<>();
		channelMongoBeans = this.getChannelMongoModel().findChannelByListUser(users);
		if (channelMongoBeans.size() > 0) {
			getLogger().debug("find channel in db");
			ChannelMongoBean channelMongoBean = channelMongoBeans.get(0);
			if (channelMongoBean != null) {
				String currentChannelId = channelMongoBean.getId();
				if(currentChannelId.contains("bot")){
					return channelMongoBean;
				}else {
					String newChannelId = "bot_"+channelMongoBean.getId();
					this.getChannelMongoModel().updateBotChannelId(currentChannelId, newChannelId);
					channelMongoBean.setId(newChannelId);
					return channelMongoBean;
				}
				
			} else {
				ChannelMongoBean channel = this.getChannelMongoModel().getChannelForBot(userName);
				insertChannelToDB(channel);
				return channel;
			}

		} else {
			getLogger().debug("insert new channel");
			ChannelMongoBean channel = this.getChannelMongoModel().getChannelForBot(userName);
			insertChannelToDB(channel);
			return channel;
		}
	}

	private boolean insertChannelToDB(ChannelMongoBean channel) {
		boolean isSuccessful = false;
		if (this.getChannelMongoModel().insertChannel(channel)) {
			isSuccessful = true;
			getLogger().debug("insert new channel to db successful");
		}
		return isSuccessful;
	}

}
