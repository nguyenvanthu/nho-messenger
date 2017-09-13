package com.nho.file.router.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.file.annotation.FileCommandProcessor;
import com.nho.file.data.game.GameMongoBean;
import com.nho.file.exception.FileException;
import com.nho.file.router.FileAbstractProcessor;
import com.nho.file.statics.Config;
import com.nho.file.statics.FileField;

@FileCommandProcessor(command = { "gameUpload" })
public class GameUploadProcessor extends FileAbstractProcessor {
	// request : http://localhost:9304/nho/file/?command=gameUpload
	@Override
	public PuElement execute(PuObjectRO request) throws FileException {
		getLogger().debug("upload new game ....");
		PuObject data = (PuObject) request;
		if (!data.variableExists("game") || !data.variableExists("name") || !data.variableExists("type")) {
			getLogger().debug("invalid params");
			return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
		}
		getLogger().debug(data.getString("game"));
		String fileName = data.getString("name");
		int type = Integer.parseInt(data.getString("type"));
		try {
			FileUtils.writeStringToFile(new File(Config.GAME_PATH + fileName), data.getString("game"));
			if (insertGameDatabase(fileName, type, Config.GAME_PATH + fileName)) {
				return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 0));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}

	private boolean insertGameDatabase(String name, int type, String path) {
		GameMongoBean bean = new GameMongoBean();
		bean.setName(name);
		bean.setPathData(path);
		bean.setType(type);
		boolean success = false;
		success = this.getGameMongoModel().insertGame(bean);
		return success;
	}

}
