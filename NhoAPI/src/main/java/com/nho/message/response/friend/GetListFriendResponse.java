package com.nho.message.response.friend;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusFriend;
import com.nho.statics.StatusUser;

public class GetListFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.GET_LIST_FRIEND_RESPONSE);
	}

	private boolean successful;
	private StatusFriend statusFriend;
	private List<String> usernames;
	private List<String> displayNames;
	private List<String> avatarNames;
	private List<StatusUser> statusUsers;
	private List<Long> lastTimeOnlines;
	private Error error;

	public List<Long> getLastTimeOnlines() {
		return lastTimeOnlines;
	}

	public void setLastTimeOnlines(List<Long> lastTimeOnlines) {
		this.lastTimeOnlines = lastTimeOnlines;
	}

	public List<StatusUser> getStatusUsers() {
		return statusUsers;
	}

	public void setStatusUsers(List<StatusUser> statusUsers) {
		this.statusUsers = statusUsers;
	}

	public List<String> getAvatarNames() {
		return avatarNames;
	}

	public void setAvatarNames(List<String> avatarNames) {
		this.avatarNames = avatarNames;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public StatusFriend getStatusFriend() {
		return statusFriend;
	}

	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}

	public List<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

	public List<String> getDisplayNames() {
		return displayNames;
	}

	public void setDisplayNames(List<String> displayNames) {
		this.displayNames = displayNames;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	private List<Integer> getListStatus() {
		List<Integer> listStatus = new ArrayList<>();
		for (StatusUser status : this.statusUsers) {
			listStatus.add(status.getCode());
		}
		return listStatus;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		if (this.successful) {
			puArray.addFrom(this.statusFriend.ordinal());
			puArray.addFrom(this.usernames);
			puArray.addFrom(this.displayNames);
			puArray.addFrom(this.avatarNames);
			puArray.addFrom(getListStatus());
			puArray.addFrom(this.getLastTimeOnlines());
		} else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		if (this.successful) {
			this.statusFriend = StatusFriend.values()[puArray.remove(0).getInteger()];
			PuArray userNameArray = puArray.remove(0).getPuArray();
			if (userNameArray != null) {
				this.usernames = new ArrayList<String>();
				for (PuValue value : userNameArray) {
					this.usernames.add(value.getString());
				}
			}
			PuArray displayNameArray = puArray.remove(0).getPuArray();
			if (displayNameArray != null) {
				this.displayNames = new ArrayList<String>();
				for (PuValue value : displayNameArray) {
					this.displayNames.add(value.getString());
				}
			}
			PuArray iconTypeArray = puArray.remove(0).getPuArray();
			if (iconTypeArray != null) {
				this.avatarNames = new ArrayList<String>();
				for (PuValue value : iconTypeArray) {
					this.avatarNames.add(value.getString());
				}
			}

			PuArray statusUserArray = puArray.remove(0).getPuArray();
			if (statusUserArray != null) {
				this.statusUsers = new ArrayList<StatusUser>();
				for (PuValue value : statusUserArray) {
					this.statusUsers.add(StatusUser.fromCode(value.getInteger()));
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
}
