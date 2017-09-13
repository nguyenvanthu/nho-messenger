package com.nho.chat.router;

import com.nhb.common.BaseLoggable;
import com.nho.chat.ChatHandler;
import com.nho.chat.data.ChannelMongoModel;
import com.nho.chat.data.LiveObjectModel;
import com.nho.chat.entity.ChannelManager;
import com.nho.chat.entity.ChatManager;

public abstract class ChatAbstractProcessor extends BaseLoggable implements ChatProcessor {
	private ChatHandler context;
	private ChannelMongoModel channelMongoModel;
	private LiveObjectModel liveObjectModel;
	
	public ChatHandler getContext() {
		return context;
	}

	public void setContext(ChatHandler context) {
		this.context = context;
	}

	protected ChannelMongoModel getChannelMongoModel() {
		if (this.channelMongoModel == null) {
			this.channelMongoModel = getContext().getModelFactory().newModel(ChannelMongoModel.class);
		}
		return this.channelMongoModel;
	}
	
	protected LiveObjectModel getLiveObjectModel(){
		if(this.liveObjectModel == null){
			this.liveObjectModel = getContext().getModelFactory().newModel(LiveObjectModel.class);
		}
		return this.liveObjectModel;
	}

	protected ChannelManager getChannelManager() {
		return this.getContext().getChannelManager();
	}
	
	protected ChatManager getChatManager(){
		return this.getContext().getChatManager();
	}
}
