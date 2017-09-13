package com.nho.server.processors.login;

import com.nhb.common.data.PuObject;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.login.DeleteUserRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;

@NhoCommandProcessor(command = { MessageType.DELETE_USER })
public class DeleteUserProcessor extends AbstractNhoProcessor<DeleteUserRequest> {

	@Override
	protected void process(DeleteUserRequest request) throws Exception {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null || request.getUserName() == null) {
			getLogger().debug("user not loggin or request error");
			return;
		}
		long deleteCounter = this.getUserMongoModel().deleteUser(request.getUserName());
		if(deleteCounter > 0){
			getLogger().debug("delete user success");
		}
		deleteFriendOfUser(request.getUserName());
	}
	/*
	 * delete friend of user when delete user 
	 * send to {@link DeleteFriendProcessor}
	 */
	private void deleteFriendOfUser(String userName){
		PuObject data = new PuObject();
		data.setInteger(FriendField.COMMAND, FriendCommand.DELETE_FRIEND.getCode());
		data.setString(FriendField.SENDER_NAME, userName);
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.FRIEND_SERVER, data);
		if(result.getInteger(FriendField.STATUS) == 0){
			getLogger().debug("delete friend of user success");
		}
	}

}
