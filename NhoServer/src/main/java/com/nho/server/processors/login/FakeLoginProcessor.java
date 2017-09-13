package com.nho.server.processors.login;

import com.nhb.common.data.PuObject;
import com.nho.message.MessageType;
import com.nho.message.request.login.FakeLoginRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;
import com.nho.statics.StatusUser;

@NhoCommandProcessor(command = { MessageType.FAKE_LOGIN })
public class FakeLoginProcessor extends AbstractNhoProcessor<FakeLoginRequest> {

	@Override
	protected void process(FakeLoginRequest request) throws Exception {
		getLogger().debug("process fake login from web client");
		String userName = request.getUserName();
		PuObject response = new PuObject();
		response.setString("command", "fakeLoginResponse");
		User user = null;
		user = getUserManager().addUserIfNotExists(userName);
		if (user == null) {
			response.setBoolean("success", false);
		}
		user.addSession(request.getSessionId());
		user.setStatus(StatusUser.ONLINE);
		this.getUserManager().addNewUser(user);
		this.getUserManager().updateStatusUser(userName, StatusUser.ONLINE);
		response.setBoolean("success", false);
		this.getContext().getApi().call(HandlerCollection.NHO_MIDDLE, response);
	}

}
