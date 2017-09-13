package com.nho.message.response.friend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusFriend;

public class SearchFriendResponse extends NhoMessage {
	{
		this.setType(MessageType.SEARCH_FRIEND_RESPONSE);
	}
	private boolean successful ;
	private Error error ;
	private StatusFriend statusFriend;
	private Set<byte[]> buddyIds;
	private List<String> usernames;
	private List<String> displayNames;
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
	public StatusFriend getStatusFriend() {
		return statusFriend;
	}
	public void setStatusFriend(StatusFriend statusFriend) {
		this.statusFriend = statusFriend;
	}
	public Set<byte[]> getBuddyIds() {
		return buddyIds;
	}
	public void setBuddyIds(Set<byte[]> buddyIds) {
		this.buddyIds = buddyIds;
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
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.successful);
		puArray.addFrom(this.buddyIds);
		puArray.addFrom(this.statusFriend.ordinal());
		puArray.addFrom(this.usernames);
		puArray.addFrom(this.displayNames);
		puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.successful = puArray.remove(0).getBoolean();
		
		PuArray array = puArray.remove(0).getPuArray();
		if (array != null) {
			this.buddyIds = new HashSet<byte[]>();
			for (PuValue value : array) {
				this.buddyIds.add(value.getRaw());
			}
		}
		
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
		
		PuValue error = puArray.remove(0);
		if(error!=null && error.getType() != PuDataType.NULL){
			this.setError(Error.fromCode(error.getInteger()));
		}

	}
}
