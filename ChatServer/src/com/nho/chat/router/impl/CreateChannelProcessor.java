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
import com.nho.statics.Personality;
import com.nho.statics.Theme;

@ChatCommandProcessor(command = { ChannelCommand.CREATE_CHANNEL })
public class CreateChannelProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.INVITER_USER, PuDataType.STRING);
		data.setType(ChatField.INVITED_USER, PuDataType.STRING);
		String inviterUser = data.getString(ChatField.INVITER_USER);
		String invitedsUser = data.getString(ChatField.INVITED_USER);
		Theme theme = Theme.fromCode(data.getInteger(ChatField.THEME));
		Personality personality = Personality.fromCode(data.getInteger(ChatField.PERSONALITY));
		ChannelMongoBean channel = createChannel(inviterUser, invitedsUser,theme,personality);
		this.getChannelManager().addUserInChannel(channel.getId(), inviterUser);
		response.setString(ChatField.CHANNEL_ID, channel.getId());
		return response;
	}

	private ChannelMongoBean createChannel(String userName, String ivitedUserName,Theme theme,Personality personality) {

		List<String> users = new ArrayList<>();
		users.add(userName);
		users.add(ivitedUserName);
		List<ChannelMongoBean> channelMongoBeans = new ArrayList<>();
		channelMongoBeans = this.getChannelMongoModel().findChannelByListUser(users);
		if (channelMongoBeans.size() > 0) {
			getLogger().debug("find channel in db");
			ChannelMongoBean channelMongoBean = channelMongoBeans.get(0);
			if (channelMongoBean != null) {
				getLogger().debug("channel id is " + channelMongoBean.getId());
				return channelMongoBean;
			} else {
				ChannelMongoBean channel = this.getChannelMongoModel().getPrivateChannelFor(userName, ivitedUserName,theme,personality);
				insertChannelToDB(channel);
				return channel;
			}

		} else {
			getLogger().debug("insert new channel");
			ChannelMongoBean channel = this.getChannelMongoModel().getPrivateChannelFor(userName, ivitedUserName,theme,personality);
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
