package com.nho.server.processors.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.chat.router.impl.GetChannelByUserProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.channel.ChangeThemeColorRequest;
import com.nho.message.response.channel.ChangeThemeColorResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.helper.ChannelHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;

/**
 * Handle change theme color request from user A send
 * {@link ChangeThemeColorResponse} to users who are chat with A
 */
@NhoCommandProcessor(command = { MessageType.CHANGE_THEME_COLOR })
public class ChangeThemeColorProcessor extends AbstractNhoProcessor<ChangeThemeColorRequest> {

	@Override
	protected void process(ChangeThemeColorRequest request) throws Exception {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null) {
			getLogger().debug("user not login ");
			return;
		}
		ChannelHelper helper = new ChannelHelper(getContext());
		ChangeThemeColorResponse response = new ChangeThemeColorResponse();
		response.setPersonality(request.getPersonality());
		response.setTheme(request.getTheme());
		response.setUserName(user.getUserName());
		for (String channelId : getChannelsByUser(request.getUser())) {
			Set<String> subs = helper.getUsersInChannelByChannelId(channelId, user.getUserName());
			response.setChannelId(channelId);
			this.sendToUserNames(response, subs);
		}
	}
	/**
	 * get channelIds of user A by userName 
	 * send command {@link ChannelCommand#GET_CHANNEL_BY_USER} to {@link GetChannelByUserProcessor}
	 * @param userName
	 */
	private List<String> getChannelsByUser(String userName) {
		List<String> channels = new ArrayList<>();
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.GET_CHANNEL_BY_USER.getCode());
		data.setString(ChatField.USER_NAME, userName);
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		if (result.getInteger(ChatField.STATUS) == 0) {
			PuArray array = result.getPuArray(ChatField.CHANNELS);
			for (PuValue value : array) {
				channels.add(value.getString());
			}
		}

		return channels;
	}
}
