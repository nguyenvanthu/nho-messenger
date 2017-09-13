package com.nho.server.processors.login;

import java.util.Set;

import com.nho.message.MessageType;
import com.nho.message.request.login.UpdateProfile;
import com.nho.message.response.login.UpdateProfileFriendResponse;
import com.nho.message.response.login.UpdateProfileResponse;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.helper.FriendHelper;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.Error;
import com.nho.uams.statics.ActivityType;

@NhoCommandProcessor(command = { MessageType.UPDATE_PROFILE })
public class UpdateProfileProcessor extends AbstractNhoProcessor<UpdateProfile> {

	@Override
	protected void process(UpdateProfile request) throws Exception {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if (user == null) {
			getLogger().debug("user not login");
			return;
		}
		getLogger().debug("user {} update profile", user.getUserName());
		FriendHelper helper = new FriendHelper(getContext());
		String newAvatarName = request.getNewAvatar();
		String newDisplayName = request.getNewDisplayName();
		boolean success = isUpdateInDatabaseSuccess(user.getUserName(), newDisplayName);
		if (success) {
			sendUpdateResponseToUser(newAvatarName, newDisplayName, user);
			sendUpdateResponseToFriend(helper, newAvatarName, newDisplayName, user);
			sendUpdateProfileLog(request, user);
		} else {
			sendError(user);
		}
	}

	public boolean isUpdateInDatabaseSuccess(String userName, String newDisplayName) {
		long modifiCount = this.getUserMongoModel().updateDisplayName(userName,newDisplayName);
		if (modifiCount > 0) {
			return true;
		}
		return false;
	}

	private void sendError(User user) {
		UpdateProfileResponse response = new UpdateProfileResponse();
		response.setSuccess(false);
		response.setError(Error.UPDATE_FAIL);
		this.sendToUser(response, user);
	}

	private void sendUpdateResponseToUser(String newAvatarName, String newDisplayName, User user) {
		getLogger().debug("send update profile response");
		UpdateProfileResponse response = new UpdateProfileResponse();
		response.setSuccess(true);
		response.setNewAvatarName(newAvatarName);
		response.setNewDisplayName(newDisplayName);
		this.sendToUser(response, user);
	}

	private void sendUpdateResponseToFriend(FriendHelper helper, String newAvatarName, String newDisplayName,
			User user) {
		Set<String> friends = helper.getFriends(user.getUserName());
		getLogger().debug("send update profile response to {} friends", friends.size());
		UpdateProfileFriendResponse response = new UpdateProfileFriendResponse();
		response.setNewAvatar(newAvatarName);
		response.setNewDisplayName(newDisplayName);
		response.setUserName(user.getUserName());

		this.sendToUserNames(response, friends);
	}

	private void sendUpdateProfileLog(UpdateProfile request, User user) {
		String content = "user " + user.getUserName() + " update profile with newDisPlayName is "
				+ request.getNewDisplayName() + " && new Avatar: " + request.getNewAvatar();
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.UPDATE_PROFILE, user.getUserName(), request.getSessionId());
	}
}
