package com.nho.server;

import java.util.UUID;

import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.mario.entity.message.SocketMessage;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.message.MessageType;
import com.nho.server.statics.HandlerCollection;

public class NhoMiddle extends BaseMessageHandler {
	@Override
	public void init(PuObjectRO initParams) {
		getLogger().debug("starting NhoMiddle server ...");
	}

	@Override
	public PuElement handle(Message message) {
		if (message instanceof SocketMessage) {
			final SocketMessage socketMessage = (SocketMessage) message;
			String sessionId = socketMessage.getSessionId();
			switch (socketMessage.getSocketMessageType()) {
			case OPENED:
				getLogger().debug("New session opened: " + sessionId);
				fakeLogin(sessionId);
				// create user here 
				break;
			case CLOSED:
				break;
			case MESSAGE:
				PuElement data = message.getData();
				break;
			}
		}
		return null;
	}
	
	private void fakeLogin(String sessionId){
		String userName = UUID.randomUUID().toString();
		PuObject obj = new PuObject();
		obj.setInteger("messageType", MessageType.FAKE_LOGIN.getId());
		obj.setString("command", "fakeLogin");
		obj.setString("sessionId", sessionId);
		obj.setString("userName", userName);
		this.getApi().call(HandlerCollection.NHO_SERVER, obj);
		
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		PuObject data = (PuObject) requestParams;
		if (data.variableExists("command")) {
			String command = data.getString("command");
			
		}
		return null;
	}
}
