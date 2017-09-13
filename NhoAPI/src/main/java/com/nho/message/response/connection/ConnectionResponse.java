package com.nho.message.response.connection;

import com.nho.message.MessageType;
import com.nho.message.NhoMessage;

public class ConnectionResponse extends NhoMessage {

	{
		this.setType(MessageType.CONNECTION_RESPONSE);
	}

	private boolean successful;

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

}
