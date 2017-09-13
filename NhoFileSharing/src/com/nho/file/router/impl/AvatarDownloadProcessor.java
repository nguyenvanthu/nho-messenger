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
import com.nho.file.statics.FileField;

@FileCommandProcessor(command = { "avatarDownload" })
public class AvatarDownloadProcessor extends FileAbstractProcessor {

	@Override
	public PuElement execute(PuObjectRO request) throws FileException {
		PuObject data = (PuObject) request;
		if (data.variableExists("userId")) {
			// get avatar by userId
			String userId = data.getString("userId");
			AvatarMongoBean bean = this.getAvatarMongoModel().getByUserId(userId);
			try {
				getLogger().debug("file path: " + bean.getPath());
				String content = FileUtils.readFileToString(new File(bean.getPath()));
				PuObject result = new PuObject();
				result.set("content", content);
				result.set("type", bean.getType());
				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}

}
