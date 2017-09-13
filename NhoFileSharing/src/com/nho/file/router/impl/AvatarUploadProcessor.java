package com.nho.file.router.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.file.annotation.FileCommandProcessor;
import com.nho.file.data.avatar.AvatarMongoBean;
import com.nho.file.exception.FileException;
import com.nho.file.router.FileAbstractProcessor;
import com.nho.file.statics.Config;
import com.nho.file.statics.FileField;

@FileCommandProcessor(command = { "avatarUpload" })
public class AvatarUploadProcessor extends FileAbstractProcessor {
	// request: http://localhost:9304/nho/file/?command=avatarUpload
	@Override
	public PuElement execute(PuObjectRO request) throws FileException {
		getLogger().debug("upload avatar ...");
		PuObject data = (PuObject) request;
		if (!data.variableExists("avatar") || !data.variableExists("userId") || !data.variableExists("type")
				|| !data.variableExists("name")) {
			getLogger().debug("invalid params");
			return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
		}
		String content = data.getString("avatar");
		String userId = data.getString("userId");
		String name = data.getString("name");
		int type = Integer.parseInt(data.getString("type"));
		try {
			FileUtils.writeStringToFile(new File(Config.AVATAR_PATH + name), content);
			if (insertAvatarToDatabase(userId, name, Config.STICKER_PATH + name, type)) {
				return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 0));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}

	private boolean insertAvatarToDatabase(String userId, String name, String path, int type) {
		AvatarMongoBean bean = new AvatarMongoBean();
		bean.setName(name);
		bean.setPath(path);
		bean.setUserId(userId);
		bean.setType(type);
		boolean success = false;
		success = this.getAvatarMongoModel().insertAvatar(bean);
		return success;

	}

}
