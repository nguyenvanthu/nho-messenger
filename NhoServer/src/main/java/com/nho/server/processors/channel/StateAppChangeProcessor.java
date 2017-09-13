package com.nho.server.processors.channel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.chat.router.impl.RemoveLiveObjByUserProcessor;
import com.nho.chat.router.impl.StateAppchangeProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.channel.StateAppChangeRequest;
import com.nho.message.response.channel.StateAppChangeResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChannelException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.AppState;

@NhoCommandProcessor(command = { MessageType.STATE_APP_CHANGE })
public class StateAppChangeProcessor extends AbstractNhoProcessor<StateAppChangeRequest> {

	@Override
	protected void process(StateAppChangeRequest request) {
		try {
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				getLogger().debug("error user null ");
				return;
			}
			AppState state = request.getState();
			getLogger().debug("state app of session {} change to {}", request.getSessionId(), state.toString());
			getLogger().debug("deviceToken is " + request.getDeviceToken());
			if (request.getDeviceToken() != null) {
				if (state == AppState.PAUSE) {
					this.getUserManager().removeDeviceInApp(request.getSessionId());
				} else {
					this.getUserManager().addNewDeviceInApp(request.getSessionId(), request.getDeviceToken());
				}
			}
			StateAppChangeResponse response = new StateAppChangeResponse();
			response.setUserChange(user.getUserName());
			response.setState(state);
			Set<String> subcribers = getUserInChannelByUserChangeStateApp(user.getUserName());
			this.sendToUserNames(response, subcribers);
			removeLiveObjectByUser(user.getUserName());
		} catch (Exception exception) {
			throw new ChannelException(exception.getMessage(), exception.getCause(), this.getContext());
		}
	}
	/**
	 * get rest user in channel with user who change state application
	 * send command {@link ChannelCommand#STATE_APP_CHANGE} to {@link StateAppchangeProcessor} 
	 */
	private Set<String> getUserInChannelByUserChangeStateApp(String userName) {
		Set<String> subcribers = new HashSet<>();

		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.STATE_APP_CHANGE.getCode());
		data.setString(ChatField.SENDER_NAME, userName);

		RPCFuture<PuElement> publish = getContext().getChatProducer().publish(data);
		try {
			PuElement puElement = publish.get();
			PuObject result = (PuObject) puElement;
			int status = result.getInteger(ChatField.STATUS);
			if (status == 0) {
				PuArray array = result.getPuArray(ChatField.SUBCRIBE);
				getLogger().debug("number result " + array.size());
				for (PuValue value : array) {
					if (!value.getString().equals(userName)) {
						subcribers.add(value.getString());
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
		}
		return subcribers;
	}

	/**
	 * remove liveObject by user
	 * send to {@link RemoveLiveObjByUserProcessor}
	 */
	private void removeLiveObjectByUser(String userName) {
		// remove live object is blocked by user
		PuObject removeLiveObjer = new PuObject();
		removeLiveObjer.setInteger(ChatField.COMMAND, ChannelCommand.REMOVE_OBJ_BY_USER.getCode());
		removeLiveObjer.setString(ChatField.USER_NAME, userName);
		this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, removeLiveObjer);
	}
}
