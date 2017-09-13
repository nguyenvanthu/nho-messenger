package com.nho.message.response.channel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusUserInChannel;

public class ChatInvitationResponse extends NhoMessage {

	{
		this.setType(MessageType.CHAT_INVITATION_RESPONSE);
	}

	private String from;
	private String channelId;
	private final Set<String> invitedUsers = new HashSet<>();
	private PuObject message;
	private boolean successful;
	private StatusUserInChannel statusChannel;
	private Error error;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.from);
			puArray.addFrom(this.channelId);

			puArray.addFrom(this.message);
			puArray.addFrom(this.invitedUsers);
			puArray.addFrom(this.statusChannel.getCode());
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			this.from = puArray.remove(0).getString();
			this.channelId = puArray.remove(0).getString();

			this.message = puArray.remove(0).getPuObject();
			PuValue value = puArray.remove(0);
			if (value != null && value.getType() == PuDataType.PUARRAY) {
				PuArray arr = value.getPuArray();
				for (PuValue val : arr) {
					this.invitedUsers.add(val.getString());
				}
			}
			this.statusChannel = StatusUserInChannel.fromCode(puArray.remove(0).getInteger());
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}

	public void setStatusChannel(StatusUserInChannel statusChannel) {
		this.statusChannel = statusChannel;
	}

	public StatusUserInChannel getStatusChannel() {
		return statusChannel;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public PuObject getMessage() {
		return message;
	}

	public void setMessage(PuObject message) {
		this.message = message;
	}

	public Set<String> getInvitedUsers() {
		return this.invitedUsers;
	}

	public void addInvitedUsers(Set<String> invitedUsers) {
		if (invitedUsers != null) {
			this.invitedUsers.addAll(invitedUsers);
		}
	}

	public void addInvitedUsers(String... invitedUsers) {
		if (invitedUsers != null) {
			this.invitedUsers.addAll(Arrays.asList(invitedUsers));
		}
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

}
