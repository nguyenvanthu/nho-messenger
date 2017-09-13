package com.nho.server.processors.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.friend.router.impl.GetListInvitedProcessor;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.GetListInviteFriend;
import com.nho.message.response.friend.GetListInviteFriendResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;

/**
 * process request {@link GetListInviteFriend}
 * return response {@link GetListInviteFriendResponse}
 * send command {@link FriendCommand#GET_LIST_INVITED} to {@link GetListInvitedProcessor}
 */
@NhoCommandProcessor(command={MessageType.GET_LIST_INVITE_FRIEND})
public class GetListInviteFriendProcessor extends AbstractNhoProcessor<GetListInviteFriend> {

	@Override
	protected void process(GetListInviteFriend request) {
		try {
			GetListInviteFriendResponse response = new GetListInviteFriendResponse();
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user not login ");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			getLogger().debug("start get list invited friend for user " + user.getUserName());

			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.GET_LIST_INVITED.getCode());
			data.setString(FriendField.SENDER_NAME, request.getUserName());
			data.setInteger(FriendField.STATUS, request.getStatusFriend().ordinal());
			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if (status == 0) {
					PuArray array = result.getPuArray(FriendField.LIST_FRIEND);
					List<String> users = new ArrayList<>();
					if (array != null) {
						for (PuValue value : array) {
							users.add(value.getString());
						}
					}

					List<String> userNames = new ArrayList<>();
					List<String> displayNames = new ArrayList<>();
					List<String> avatarNames = new ArrayList<>();
					for (String value : users) {
						UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(value);
						if (userBean != null) {
							userNames.add(userBean.getUserName());
							displayNames.add(userBean.getDisplayName());
							avatarNames.add(userBean.getAvatar().getName());
						}

					}

					getLogger().debug("number friend in list invited friend  " + users.size());

					response.setSuccessful(true);
					response.setStatusFriend(request.getStatusFriend());
					response.setUsernames(userNames);
					response.setDisplayNames(displayNames);
					response.setAvatarNames(avatarNames);

					this.sendToUser(response, user);
				} else {
					getLogger().debug("something wrong in FriendServer with status = 1");
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
