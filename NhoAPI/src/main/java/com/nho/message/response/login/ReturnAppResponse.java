package com.nho.message.response.login;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusUser;

public class ReturnAppResponse extends NhoMessage {
	{
		this.setType(MessageType.RETURN_APP_RESPONSE);
	}
	private boolean success;
	private List<String> friends;
	private List<StatusUser> statusFriends;
	private List<Long> lastTimeOnlines;
	private Error error;

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.success);
		if (this.success) {
			puArray.addFrom(this.friends);
			puArray.addFrom(getListStatus());
			puArray.addFrom(this.getLastTimeOnlines());
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
		if (this.success) {
			PuArray friendArray = puArray.remove(0).getPuArray();
			if (friendArray != null) {
				this.friends = new ArrayList<String>();
				for (PuValue value : friendArray) {
					this.friends.add(value.getString());
				}
			}

			PuArray statusFriendArray = puArray.remove(0).getPuArray();
			if (statusFriendArray != null) {
				this.statusFriends = new ArrayList<StatusUser>();
				for (PuValue value : statusFriendArray) {
					this.statusFriends.add(StatusUser.fromCode(value.getInteger()));
				}
			}
			PuArray lastTimeOnlineArray = puArray.remove(0).getPuArray();
			if (lastTimeOnlineArray != null) {
				this.lastTimeOnlines = new ArrayList<Long>();
				for (PuValue value : lastTimeOnlineArray) {
					this.lastTimeOnlines.add(value.getLong());
				}
			}
		} else {
			PuValue error = puArray.remove(0);
			if (error != null && error.getType() != PuDataType.NULL) {
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}

	private List<Integer> getListStatus() {
		List<Integer> listStatus = new ArrayList<>();
		for (StatusUser status : this.statusFriends) {
			listStatus.add(status.getCode());
		}
		return listStatus;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<String> getFriends() {
		return friends;
	}

	public void setFriends(List<String> friends) {
		this.friends = friends;
	}

	public List<StatusUser> getStatusFriends() {
		return statusFriends;
	}

	public void setStatusFriends(List<StatusUser> statusFriends) {
		this.statusFriends = statusFriends;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public List<Long> getLastTimeOnlines() {
		return lastTimeOnlines;
	}

	public void setLastTimeOnlines(List<Long> lastTimeOnlines) {
		this.lastTimeOnlines = lastTimeOnlines;
	}
}
