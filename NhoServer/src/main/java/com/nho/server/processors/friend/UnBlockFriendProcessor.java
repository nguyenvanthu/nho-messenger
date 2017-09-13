package com.nho.server.processors.friend;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.UnBlockFriendRequest;
import com.nho.message.response.friend.UnBlockFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;

/**
 * process request {@link UnBlockFriendRequest}
 * return response {@link UnBlockFriendResponse}
 * send command {@link FriendCommand#UNBLOCK_FRIEND} to {@link com.nho.friend.router.impl.UnbockFriendProcessor}
 */
@NhoCommandProcessor(command={MessageType.UNBLOCK_FRIEND})
public class UnBlockFriendProcessor extends AbstractNhoProcessor<UnBlockFriendRequest> {

	@Override
	protected void process(UnBlockFriendRequest request) {
		try {
			User user = getUserManager().getUserBySessionId(request.getSessionId());
			UnBlockFriendResponse response = new UnBlockFriendResponse();
			if (user == null) {
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			getLogger().debug("user " + request.getSenderUserName() + " unblock user " + request.getBlockedUserName());

			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.UNBLOCK_FRIEND.getCode());
			data.setString(FriendField.SENDER_NAME, request.getSenderUserName());
			data.setString(FriendField.BLOCKED_NAME, request.getBlockedUserName());

			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if (status == 0) {
					UserMongoBean blocked = this.getUserMongoModel().findByUserName(request.getBlockedUserName());
					UserMongoBean senderBean = this.getUserMongoModel().findByUserName(request.getSenderUserName());
					response.setSuccessful(true);
					response.setBlockedDisplayName(blocked.getDisplayName());
					response.setBlockedUserName(request.getBlockedUserName());
					response.setAvatarBlockedName(blocked.getAvatar().getName());

					response.setSenderDisplayName(senderBean.getDisplayName());
					response.setSenderUserName(senderBean.getUserName());
					response.setAvatarSenderName(senderBean.getAvatar().getName());
					this.send(response, user.getSessions());
					this.sendToUserName(response, request.getSenderUserName());
				} else {
					response.setSuccessful(false);
					response.setError(Error.fromCode(result.getInteger(FriendField.ERROR)));
					this.send(response, request.getSessionId());
					return;
				}
			} catch (InterruptedException | ExecutionException e) {
				getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
				response.setSuccessful(false);
				response.setError(Error.ERROR_RABBIT_MQ);
				this.send(response, request.getSessionId());
				return;
			}
		} catch (Exception exception) {
			throw new FriendException(exception.getMessage(), exception.getCause(),this.getContext());
		}
	}
}
