package com.nho.file.router;

import com.nhb.common.BaseLoggable;
import com.nho.file.FileHandler;
import com.nho.file.data.AvatarMongoModel;
import com.nho.file.data.GameMongoModel;
import com.nho.file.data.StickerMongoModel;

public abstract class FileAbstractProcessor extends BaseLoggable implements FileProcessor {
	private FileHandler context;
	private AvatarMongoModel avatarModel;
	private GameMongoModel gameModel;
	private StickerMongoModel stickerModel;

	public FileHandler getContext() {
		return context;
	}

	public void setContext(FileHandler context) {
		this.context = context;
	}

	protected AvatarMongoModel getAvatarMongoModel() {
		if (this.avatarModel == null) {
			this.avatarModel = getContext().getModelFactory().newModel(AvatarMongoModel.class);
		}
		return this.avatarModel;
	}

	protected GameMongoModel getGameMongoModel() {
		if (this.gameModel == null) {
			this.gameModel = getContext().getModelFactory().newModel(GameMongoModel.class);
		}
		return this.gameModel;
	}

	protected StickerMongoModel getStickerMongoModel() {
		if (this.stickerModel == null) {
			this.stickerModel = getContext().getModelFactory().newModel(StickerMongoModel.class);
		}
		return this.stickerModel;
	}

}
