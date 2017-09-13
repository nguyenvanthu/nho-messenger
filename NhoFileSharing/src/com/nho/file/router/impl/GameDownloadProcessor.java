package com.nho.file.router.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.file.annotation.FileCommandProcessor;
import com.nho.file.data.game.GameMongoBean;
import com.nho.file.exception.FileException;
import com.nho.file.router.FileAbstractProcessor;
import com.nho.file.statics.FileField;

@FileCommandProcessor(command = { "gameDownload" })
public class GameDownloadProcessor extends FileAbstractProcessor {
	// request: http://localhost:9304/nho/file/?command=gameDownload
	@Override
	public PuElement execute(PuObjectRO request) throws FileException {
		getLogger().debug("receive request get game data ...");
		PuObject data = (PuObject) request;
		if (data.variableExists("type")) {
			// get game by type
			int type = Integer.parseInt(data.getString("type"));
			GameMongoBean bean = this.getGameMongoModel().getGameByType(type);
			try {
				getLogger().debug("file path: "+bean.getPathData());
				String content = FileUtils.readFileToString(new File(bean.getPathData()));
				PuObject result = new PuObject();
				result.set("content", content);
				result.set("name", bean.getName());
				result.set("type", bean.getType());
				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (data.variableExists("name")) {
			// get game by name
			String name = data.getString("name");
			getLogger().debug(name);
			GameMongoBean bean = this.getGameMongoModel().getGameByName(name);
			try {
				getLogger().debug("file path: "+bean.getPathData());
				String content = FileUtils.readFileToString(new File(bean.getPathData()));
				PuObject result = new PuObject();
				result.set("content", content);
				result.set("name", bean.getName());
				result.set("type", bean.getType());
				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// get all game
			List<GameMongoBean> beans = this.getGameMongoModel().getAllGames();
			PuObject result = new PuObject();
			for(GameMongoBean bean : beans){
				try {
					String content = FileUtils.readFileToString(new File(bean.getPathData()));
					PuObject sub = new PuObject();
					sub.set("content", content);
					sub.set("name", bean.getName());
					sub.set("type", bean.getType());
					result.set(bean.getName(), sub);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return result;
		}
		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}

}
