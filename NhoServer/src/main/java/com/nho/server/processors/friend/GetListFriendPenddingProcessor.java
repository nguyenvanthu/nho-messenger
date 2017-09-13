package com.nho.server.processors.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.friend.router.impl.GetListPenddingProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.GetListFriendPendding;
import com.nho.message.response.friend.GetListFriendPenddingResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;

/**
 * process request {@link GetListFriendPendding}
 * return response {@link GetListFriendPenddingResponse}
 * send command {@link FriendCommand#GET_LIST_PENDDING} to {@link GetListPenddingProcessor}
 */
@NhoCommandProcessor(command={MessageType.GET_LIST_FRIEND_PENDDING})
public class GetListFriendPenddingProcessor extends AbstractNhoProcessor<GetListFriendPendding> {

	@Override
	protected void process(GetListFriendPendding request) {
		try {
			GetListFriendPenddingResponse response = new GetListFriendPenddingResponse();
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user not login ");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			// getLogger().debug("start get list friend pending for user " +
			// user.getUserName());

			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_PENDDING.getCode());
			data.setString(FriendField.SENDER_NAME, request.getSenderUserName());
			data.setInteger(FriendField.STATUS, request.getStatus().ordinal());

			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if (status == 0) {
					PuArray array = result.getPuArray(FriendField.LIST_FRIEND);

					List<String> buddyNames = new ArrayList<>();
					if (array != null) {
						for (PuValue value : array) {
							buddyNames.add(value.getString());
						}
					}
					List<String> userNames = new ArrayList<>();
					List<String> displayNames = new ArrayList<>();
					List<String> avatarNames = new ArrayList<>();
					for (String buddyName : buddyNames) {
						UserMongoBean buddy = this.getUserMongoModel().findByFacebookId(buddyName);
						if (buddy != null) {
							userNames.add(buddy.getUserName());
							displayNames.add(buddy.getDisplayName());
							avatarNames.add(buddy.getAvatar().getName());
						}
					}
					getLogger().debug("number friend in list pendding " + buddyNames.size());

					response.setSuccessful(true);
					response.setStatusFriend(request.getStatus());
					response.setUsernames(userNames);
					response.setDisplayNames(displayNames);
					response.setAvatarNames(avatarNames);

					this.sendToUser(response, user);
				} else {
					getLogger().debug("something wrong in FriendServer with status = 1 ");
				}
			} catch (InterruptedException | ExecutionException e) {
				getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
				response.setSuccessful(false);
				response.setError(Error.ERROR_RABBIT_MQ);
				this.send(response, request.getSessionId());
				return;
			}
		} catch (Exception exception) {
			throw new FriendException(exception.getMessage(), exception.getCause(), this.getContext());
		}

	}
}
