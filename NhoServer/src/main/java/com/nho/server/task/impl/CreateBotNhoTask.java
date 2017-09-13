package com.nho.server.task.impl;

import com.nho.server.NhoServer;
import com.nho.server.entity.avatar.Avatar;
import com.nho.server.statics.BotNho;
import com.nho.server.task.AbstractTask;
import com.nho.statics.AvatarType;

public class CreateBotNhoTask extends AbstractTask {
	public CreateBotNhoTask(NhoServer context) {
		super.setContext(context);
	}
	@Override
	public void run() {
		if (!this.getUserMongoModel().isExistUser(BotNho.USER_NAME)) {
			Avatar avatar = new Avatar();
			avatar.setName(BotNho.AVATAR);
			avatar.setType(AvatarType.ICON);
			avatar.setUrl("url_iconbot");
			this.getUserMongoModel().insert(BotNho.USER_NAME, BotNho.PASSWORD, BotNho.DISPLAY_NAME,
					avatar, BotNho.CHECKSUM);
			
		}
		this.getContext().getUserManager().addUserIfNotExists(BotNho.USER_NAME);
	}

}
