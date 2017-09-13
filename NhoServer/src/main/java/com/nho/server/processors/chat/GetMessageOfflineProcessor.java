package com.nho.server.processors.chat;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.request.chat.ChatMessage;
import com.nho.message.request.chat.GetMessageOfflines;
import com.nho.message.response.GetMessageOfflineResponses;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.exception.ChatException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.ChatField;
import com.nho.statics.Error;

@NhoCommandProcessor(command = { MessageType.GET_MESSAGE_OFFLINE })
public class GetMessageOfflineProcessor extends AbstractNhoProcessor<GetMessageOfflines> {

	@Override
	protected void process(GetMessageOfflines request) {
		try {
			getLogger().debug("start get list message offlines  ....");
			GetMessageOfflineResponses response = new GetMessageOfflineResponses();
			User user = this.getUserManager().getUserBySessionId(request.getSessionId());
			if (user == null) {
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			getLogger().debug("get list message offline of user " + request.getUserName());
			List<PuObject> messageOfflines = getListMessageOffline(request.getUserName());
			if (messageOfflines == null) {
				getLogger().debug("error ");
				return;
			}
			getLogger().debug("number message " + messageOfflines.size());
			response.setSuccessful(true);
			response.setError(null);
			response.setMessageOfflines(messageOfflines);
			getLogger().debug("send list message offline to user " + user.getUserName());
			this.sendToUser(response, user);
		} catch (Exception exception) {
			throw new ChatException(exception.getMessage(), exception.getCause(), this.getContext());
		}

	}

	private List<PuObject> getListMessageOffline(String user) {
		try {
			List<PuObject> results = new ArrayList<>();
			List<ChatMessage> messageOfflines = this.getUserManager().getListMessageOfflineOfUser(user);
			for (ChatMessage message : messageOfflines) {
				PuObject obj = new PuObject();
				obj.setString(ChatField.FROM, message.getFrom());
				obj.setString(ChatField.TO, message.getTo());
				obj.setLong(ChatField.SENT_TIME, message.getSentTime());
				obj.setPuObject(ChatField.DATA, message.getData());
				results.add(obj);
			}
			return results;
		} catch (Exception exception) {
			getLogger().debug("error " + exception);
			return null;
		}

	}

}
