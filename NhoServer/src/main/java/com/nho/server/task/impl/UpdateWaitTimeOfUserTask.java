package com.nho.server.task.impl;

import java.util.Set;

import com.nhb.common.data.PuObject;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.server.NhoServer;
import com.nho.server.statics.HandlerCollection;
import com.nho.server.task.AbstractTask;

/**
 * update wait time of user chat with boot
 * 
 * @author nothing
 *
 */
public class UpdateWaitTimeOfUserTask extends AbstractTask {
	public UpdateWaitTimeOfUserTask(NhoServer context) {
		super.setContext(context);
	}

	@Override
	public void run() {
		try {
			Set<String> userChatWithBots = this.getContext().getUserManager().getUserChatWithBots();
			if (userChatWithBots.size() > 0) {
				for (String userName : userChatWithBots) {
					PuObject updateWaitTimer = new PuObject();
					updateWaitTimer.setInteger(ChatField.COMMAND, ChannelCommand.UPDATE_WAIT_TIME.getCode());
					updateWaitTimer.setString(ChatField.USER_NAME, userName);
					this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, updateWaitTimer);
				}
			}
		} catch (Exception exception) {
			getLogger().debug("exception when update waitime of user with bot: " + exception);
		}
	}
}
