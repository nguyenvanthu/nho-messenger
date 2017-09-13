package com.nho.file.router.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.file.annotation.FileCommandProcessor;
import com.nho.file.data.sticker.StickerMongoBean;
import com.nho.file.exception.FileException;
import com.nho.file.router.FileAbstractProcessor;
import com.nho.file.statics.Config;
import com.nho.file.statics.FileField;

@FileCommandProcessor(command = { "stickerUpload" })
public class StickerUploadProcessor extends FileAbstractProcessor {
	// request : http://localhost:9304/nho/file/?command=stickerUpload
	@Override
	public PuElement execute(PuObjectRO request) throws FileException {
		getLogger().debug("upload new sticker .....");
		PuObject data = (PuObject) request;
		if (!data.variableExists("sticker") || !data.variableExists("type") || !data.variableExists("name")
				|| !data.variableExists("group")) {
			getLogger().debug("invalid params");
			return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
		}
		int type = Integer.parseInt(data.getString("type"));
		String content = data.getString("sticker");
		String name = data.getString("name");
		String group = data.getString("group");
		try {
			FileUtils.writeStringToFile(new File(Config.STICKER_PATH + name), content);
			if (insertStickerDatabase(name, type, group,Config.STICKER_PATH + name)) {
				return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 0));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}
	private boolean insertStickerDatabase(String name,int type,String group,String path){
		StickerMongoBean sticker = new StickerMongoBean();
		sticker.setGroup(group);
		sticker.setName(name);
		sticker.setPath(path);
		sticker.setType(type);
		boolean success = false ;
		success = this.getStickerMongoModel().insertSticker(sticker);
		return success;
	}
}
