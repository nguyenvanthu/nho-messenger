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
import com.nho.file.data.sticker.StickerMongoBean;
import com.nho.file.exception.FileException;
import com.nho.file.router.FileAbstractProcessor;
import com.nho.file.statics.FileField;

@FileCommandProcessor(command = { "stickerDownload" })
public class StickerDownloadProcessor extends FileAbstractProcessor {
	// request : http://localhost:9304/nho/file/?command=stickerDownload
	@Override
	public PuElement execute(PuObjectRO request) throws FileException {
		PuObject data = (PuObject) request;
		if (data.variableExists("group")) {
			// get sticker by group
			String group = data.getString("group");
			List<StickerMongoBean> beans = this.getStickerMongoModel().getByGroup(group);
			PuObject result = new PuObject();
			for (StickerMongoBean bean : beans) {
				try {
					String content = FileUtils.readFileToString(new File(bean.getPath()));
					PuObject sub = new PuObject();
					sub.set("content", content);
					sub.set("name", bean.getName());
					sub.set("type", bean.getType());
					sub.set("group", bean.getGroup());
					result.set(bean.getName(), sub);
					return result;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// get all stickers
			List<StickerMongoBean> beans = this.getStickerMongoModel().getAllStickers();
			PuObject result = new PuObject();
			for (StickerMongoBean bean : beans) {
				try {
					String content = FileUtils.readFileToString(new File(bean.getPath()));
					PuObject sub = new PuObject();
					sub.set("content", content);
					sub.set("name", bean.getName());
					sub.set("type", bean.getType());
					sub.set("group", bean.getGroup());
					result.set(bean.getName(), sub);
					return result;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return PuObject.fromObject(new MapTuple<>(FileField.STATUS, 1));
	}

}
