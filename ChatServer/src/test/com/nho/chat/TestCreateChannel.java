package test.com.nho.chat;

import com.nho.chat.ChatHandler;
import com.nho.chat.data.ChannelMongoModel;

public class TestCreateChannel {
	private ChatHandler context;
	private ChannelMongoModel channelMongoModel;

	public ChatHandler getContext() {
		return context;
	}

	public void setContext(ChatHandler context) {
		this.context = context;
	}

	public TestCreateChannel() {
		
	}

	protected ChannelMongoModel getChannelMongoModel() {
		if (this.channelMongoModel == null) {
			this.channelMongoModel = getContext().getModelFactory().newModel(ChannelMongoModel.class);
		}
		return this.channelMongoModel;
	}

	public static void main(String[] args) {

	}
}
