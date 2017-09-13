package com.nho.server.processors.friend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.GetListStatusFriend;
import com.nho.message.response.friend.ListStateFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;
import com.nho.statics.StatusUser;

/**
 * process request {@link GetListStatusFriend} return response
 * {@link ListStateFriendResponse} send command
 * {@link FriendCommand#GET_LIST_FRIEND} to {@link GetListFriendProcessor} and
 * consider status user {@link StatusUser}
 */
@NhoCommandProcessor(command = { MessageType.GET_LIST_STATUS_FRIEND })
public class GetListStatusFriendProcessor extends AbstractNhoProcessor<GetListStatusFriend> {

	@Override
	protected void process(GetListStatusFriend request) {
		try {
			ListStateFriendResponse response = new ListStateFriendResponse();
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user not login ");
				response.setSuccess(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_FRIEND.getCode());
			data.setString(FriendField.SENDER_NAME, user.getUserName());
			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if (status == 0) {
					PuArray array = result.getPuArray(FriendField.LIST_FRIEND);
					Set<String> friends = new HashSet<String>();
					if (array != null) {
						for (PuValue value : array) {
							friends.add(value.getString());
						}
					}
					response.setSuccess(true);
					response.setStatuss(getStatusOfListFriend(friends));
					response.setUserNames(friends);
					this.sendToUser(response, user);
				} else {
					getLogger().debug("something wrong in FriendServer with status = 1");
				}
			} catch (InterruptedException | ExecutionException e) {
				getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
				response.setSuccess(false);
				response.setError(Error.ERROR_RABBIT_MQ);
				this.send(response, request.getSessionId());
				return;
			}
		} catch (Exception exception) {
			throw new FriendException(exception.getMessage(), exception.getCause(), this.getContext());
		}

	}

	private List<StatusUser> getStatusOfListFriend(Set<String> userNames) {
		List<StatusUser> statuss = new ArrayList<>();
		for (String userName : userNames) {
			User user = this.getUserManager().getUserByUserName(userName);
			statuss.add(user.getStatus());
		}
		return statuss;
	}
}
