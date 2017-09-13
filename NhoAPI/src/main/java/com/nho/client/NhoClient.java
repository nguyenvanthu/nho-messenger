package com.nho.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nhb.common.data.PuArray;
import com.nhb.common.vo.HostAndPort;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;
import com.nhb.messaging.socket.SocketEvent;
import com.nhb.messaging.socket.netty.NettySocketClient;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.PingRequest;
import com.nho.message.response.connection.ConnectionResponse;
import com.nho.message.response.connection.DisconnectEvent;

public class NhoClient extends NettySocketClient {

	DelegatedReceiver receiver;
	Map<Long, Long> pingSentTimeMapping = new ConcurrentHashMap<>();
	Map<Integer, String> messageIdMapping = new ConcurrentHashMap<>();

	public NhoClient(boolean useRingBuffer) {
		this.setUseLengthPrepender(false);
		this.receiver = new DelegatedReceiver(this, useRingBuffer);
		this.receiver.start();

		super.addEventListener(SocketEvent.CONNECTED, new EventHandler() {

			@Override
			public void onEvent(Event event) throws Exception {
				ConnectionResponse connectionResponse = new ConnectionResponse();
				connectionResponse.setSuccessful(true);
				receiver.publish(connectionResponse);
			}
		});

		super.addEventListener(SocketEvent.DISCONNECTED, new EventHandler() {

			@Override
			public void onEvent(Event event) throws Exception {
				removeMessageId();
				getLogger().debug("client disconnect ....");
				DisconnectEvent disconnectResponse = new DisconnectEvent();
				receiver.publish(disconnectResponse);
			}
		});

		super.addEventListener(SocketEvent.MESSAGE, new EventHandler() {

			@Override
			public void onEvent(Event event) throws Exception {
				SocketEvent socketEvent = (SocketEvent) event;
				PuArray array = (PuArray) socketEvent.getData();
				receiver.publish(array);
			}
		});
		
		
	}

	@Override
	protected void _connect(String host, int port, boolean useSSL) {
		try {
			super._connect(host, port, useSSL);
		} catch (IOException e) {
			ConnectionResponse connectionResponse = new ConnectionResponse();
			connectionResponse.setSuccessful(false);
			dispatchEvent(new NhoEvent(connectionResponse));
		}
	}
	

	@Override
	public void close() {
		try {
			super.close();
			System.out.println("check point 1");
		} catch (Exception e) {
			getLogger().error("Error while closing socket: ", e);
		}
		System.out.println("check point 2");
		this.receiver.shutdown();
		System.out.println("check point 3");
	}

	public NhoClient() {
		this(false);
	}

	public NhoClient(HostAndPort address) {
		this();
		this.setServerAddress(address);
	}

	public NhoClient(HostAndPort address, boolean useRingBuffer) {
		this(useRingBuffer);
		this.setServerAddress(address);
	}

	public void addEventListener(MessageType messageType, EventHandler listener) {
		super.addEventListener(messageType.name(), listener);
	}

	public void send(NhoMessage message) {
		if (this.isConnected()) {
			try {
				super.send(message.serialize());
				// addNewMessageId(message);
				if (message instanceof PingRequest) {
					this.pingSentTimeMapping.put(((PingRequest) message).getId(), System.nanoTime());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Cannot send message while network has not been connected");
		}
	}

	@Override
	public void addEventListener(String eventType, EventHandler listener) {
		throw new UnsupportedOperationException(
				"Method is not supported, use addEventListener(MessageType, EventHandler)");
	}

	// private void addNewMessageId(NhoMessage message){
	// messageIdMapping.put(message.getMessageId(), message.getSessionId());
	// }
	
	private void removeMessageId() {
		List<Integer> messageIds = new ArrayList<>(messageIdMapping.keySet());
		if(messageIds.size()>0){
			this.messageIdMapping.remove(messageIds.get(messageIds.size() - 1));			
		}
	}
}
