package com.nho.server.processors.friend;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.CancelFriendRequest;
import com.nho.message.response.friend.CancelFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;

/**
 * process request {@link CancelFriendRequest}
 * return response {@link CancelFriendResponse}
 * send command {@link FriendCommand#CANCEL_FRIEND} to {@link com.nho.friend.router.impl.CancelFriendProcessor}
 */
@NhoCommandProcessor(command={MessageType.CANCEL_FRIEND})
public class CancelFriendRequestProcessor extends AbstractNhoProcessor<CancelFriendRequest> {

	@Override
	protected void process(CancelFriendRequest request) {
		try {
			User user = getUserManager().getUserBySessionId(request.getSessionId());
			CancelFriendResponse response = new CancelFriendResponse();
			if (user == null) {
				getLogger().debug("error user not login in CancelFriendProcessor");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			getLogger().debug("user {} cancel friend request from user {}", user.getUserName(),
					request.getSenderUserName());
			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.CANCEL_FRIEND.getCode());
			data.setString(FriendField.SENDER_NAME, request.getSenderUserName());
			data.setString(FriendField.CANCELER_NAME, request.getCancelerUserName());
			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if (status == 0) {
					User canceler = this.getUserManager().getUserByUserName(request.getCancelerUserName());
					UserMongoBean senderBean = this.getUserMongoModel().findByUserName(request.getSenderUserName());
					UserMongoBean cancelerBean = this.getUserMongoModel()
							.findByUserName(request.getCancelerUserName());

					response.setSuccessful(true);
					response.setSenderDisplayName(senderBean.getDisplayName());
					response.setSenderUserName(senderBean.getUserName());
					response.setStatusFriend(request.getStatusFriend());
					response.setAvatarSenderName(senderBean.getAvatar().getName());

					response.setAvatarCancelerName(cancelerBean.getAvatar().getName());
					response.setCancelerDisplayName(cancelerBean.getDisplayName());
					response.setCancelerUserName(cancelerBean.getUserName());
					this.sendToUser(response, canceler);
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
