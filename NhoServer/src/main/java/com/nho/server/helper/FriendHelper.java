package com.nho.server.helper;

import java.util.HashSet;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.server.NhoServer;
import com.nho.server.statics.HandlerCollection;

public class FriendHelper extends AbstractHelper {
	public FriendHelper(NhoServer context) {
		super.setContext(context);
	}

	public Set<String> getFriends(String userName) {
		Set<String> friends = new HashSet<String>();
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_FRIEND.getCode());
		data.setString(FriendField.SENDER_NAME, userName);
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
		if (result == null) {
			getLogger().debug("respone from friend server null");
		}
		int status = result.getInteger(FriendField.STATUS);
		if (status == 0) {
			PuArray array = result.getPuArray(FriendField.LIST_FRIEND);
			if (array != null) {
				for (PuValue value : array) {
					friends.add(value.getString());
				}
			}
		}
		return friends;
	}
}
