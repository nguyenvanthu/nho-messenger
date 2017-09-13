package com.nho.chat.router.impl;

import java.util.List;

import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.chat.annotation.ChatCommandProcessor;
import com.nho.chat.data.ChannelMongoBean;
import com.nho.chat.data.UserInChannelBean;
import com.nho.chat.exception.ChatException;
import com.nho.chat.router.ChatAbstractProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.statics.Error;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

@ChatCommandProcessor(command = { ChannelCommand.JOIN_CHANNEL })
public class JoinChannelProcessor extends ChatAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws ChatException {
		PuObject response = new PuObject();
		PuObject data = (PuObject) request;
		data.setType(ChatField.CHANNEL_ID, PuDataType.STRING);
		data.setType(ChatField.JOINER_USER, PuDataType.STRING);
		String channelId = data.getString(ChatField.CHANNEL_ID);
		String userName = data.getString(ChatField.JOINER_USER);
		Theme theme = Theme.fromCode(data.getInteger(ChatField.THEME));
		Personality personality = Personality.fromCode(data.getInteger(ChatField.PERSONALITY));
		this.getChannelMongoModel().updateThemeAndPersonlityUser(channelId, userName, theme, personality);

		ChannelMongoBean channel = this.getChannelMongoModel().findChannelById(channelId);
		if (channel == null) {
			response.setInteger(ChatField.STATUS, 1);
			response.setInteger(ChatField.ERROR, Error.CHANNEL_NOT_FOUND.getCode());
			return response;
		}
		this.getChannelManager().addUserInChannel(channel.getId(), userName);
		String inviteChatUser = "";
		int themeInviteChatUser = 0;
		int personalityInviteChatUser = 0;
		for (UserInChannelBean userBean : channel.getUsers()) {
			if(!userBean.getUserName().equals(userName)){
				inviteChatUser = userBean.getUserName();
				themeInviteChatUser = userBean.getTheme().getCode();
				personalityInviteChatUser = userBean.getPersonality().getCode();
			}
		}
		boolean isInviteChatUserBusy = isInviteChatUserBuzy(inviteChatUser,channelId);
		response.setInteger(ChatField.STATUS, 0);
		response.setString(ChatField.INVITER_USER, inviteChatUser);
		response.setInteger(ChatField.THEME, themeInviteChatUser);
		response.setInteger(ChatField.PERSONALITY, personalityInviteChatUser);
		response.setBoolean(ChatField.IS_BUSY, isInviteChatUserBusy);
		return response;
	}
	private boolean isInviteChatUserBuzy(String userInviteChat, String channelId) {
		boolean isBuzy = false;
		List<String> currentChannelIds = this.getChannelManager().getChannelIdByUserName(userInviteChat);
		if(currentChannelIds.size()>0){
			for (String currentChannelId : currentChannelIds) {
				if (!currentChannelId.equals(channelId)) {
					isBuzy = true;
				}
			}
		}else {
			isBuzy = true;
		}
		return isBuzy;
	}
}
