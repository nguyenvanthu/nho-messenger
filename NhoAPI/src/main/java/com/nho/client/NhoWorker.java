package com.nho.client;

import com.lmax.disruptor.WorkHandler;
import com.nhb.common.BaseLoggable;
import com.nhb.common.Loggable;
import com.nhb.common.data.PuArray;
import com.nho.message.MessageEvent;
import com.nho.message.NhoMessage;
import com.nho.message.response.connection.PongEvent;

public class NhoWorker extends BaseLoggable implements WorkHandler<MessageEvent>, Runnable, Loggable {

	private NhoClient client;
	private MessageEvent socketData;

	public NhoWorker(NhoClient client) {
		this.client = client;
	}

	public NhoWorker(NhoClient client, MessageEvent data) {
		this(client);
		this.socketData = data;
	}

	@Override
	public void onEvent(MessageEvent event) throws Exception {
		NhoMessage message = (event.getData() instanceof NhoMessage) ? (NhoMessage) event.getData()
				: NhoMessage.deserialize((PuArray) event.getData(), null);
		if (message instanceof PongEvent) {
			long pingTime = this.client.pingSentTimeMapping.remove(((PongEvent) message).getId());
			((PongEvent) message).setPingTime(pingTime);
		}
		this.client.dispatchEvent(new NhoEvent(message));
	}

	@Override
	public void run() {
		try {
			this.onEvent(this.socketData);
		} catch (Exception e) {
			this.client.receiver.handleEventException(e, -1, this.socketData);
		}
	}
}
