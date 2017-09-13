package com.nho.server.processors.friend;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.router.impl.IgnoreFriendProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.IgnoreFriendRequest;
import com.nho.message.response.friend.IgnoreFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;

/**
 * process request {@link IgnoreFriendRequest}
 * return response {@link IgnoreFriendResponse}
 * send command {@link FriendCommand#IGNORE_FRIEND} to {@link IgnoreFriendProcessor}
 */
@NhoCommandProcessor(command={MessageType.IGNORE_FRIEND_REQUEST})
public class IgnoreFriendRequestProcessor extends AbstractNhoProcessor<IgnoreFriendRequest> {

	@Override
	protected void process(IgnoreFriendRequest request) {
		try {
			User user = getUserManager().getUserBySessionId(request.getSessionId());
			IgnoreFriendResponse response = new IgnoreFriendResponse();
			if (user == null) {
				getLogger().debug("error user not login in IgnoreFriendProcessor");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			getLogger().debug("user {} ignore friend request from user {}", user.getUserName(),
					request.getSenderUserName());
			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.IGNORE_FRIEND.getCode());
			data.setString(FriendField.SENDER_NAME, request.getSenderUserName());
			data.setString(FriendField.IGNORER_NAME, request.getIgnorerUserName());

			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if (status == 0) {
					UserMongoBean ignoreBean = this.getUserMongoModel().findByUserName(request.getIgnorerUserName());
					UserMongoBean senderBean = this.getUserMongoModel().findByUserName(request.getSenderUserName());
					getLogger().debug("delete in database success");
					response.setSuccessful(true);
					response.setIgnoreDisplayName(ignoreBean.getDisplayName());
					response.setIgnoreUsername(ignoreBean.getUserName());
					response.setStatusFriend(request.getStatusFriend());
					response.setAvatarInogerName(ignoreBean.getAvatar().getName());

					response.setSenderDisplayName(senderBean.getDisplayName());
					response.setSenderUserName(senderBean.getUserName());
					response.setAvatarSenderName(senderBean.getAvatar().getName());
					getLogger().debug("send ignore response to " + user.getUserName());
					this.sendToUser(response, user);
					this.sendToUserName(response, request.getSenderUserName());
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
