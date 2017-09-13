package com.nho.message.request.chat.anything;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;
import com.nho.statics.GameType;

public class PlayGameRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.PLAY_GAME);
	}
	
	private GameType gameType;
	private String channelId;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.gameType.getCode());
		puArray.addFrom(this.channelId);
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.gameType = GameType.fromCode(puArray.remove(0).getInteger());
		this.channelId = puArray.remove(0).getString();
	}

}
