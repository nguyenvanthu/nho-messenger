package com.nho.server.processors.login;

import com.nho.message.MessageType;
import com.nho.message.request.login.UpdateFacebookToken;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.processors.impl.AbstractNhoProcessor;

@NhoCommandProcessor(command = { MessageType.UPDATE_TOKEN })
public class UpdateFacebookTokenProcessor extends AbstractNhoProcessor<UpdateFacebookToken> {

	@Override
	protected void process(UpdateFacebookToken request) throws Exception {
		String facebookId = request.getFacebookId();
		String facebookToken = request.getFacebookToken();
		if (facebookId == null || facebookToken == null) {
			getLogger().debug("facebookId or facebookToken null");
			return;
		}
		UserMongoBean userBean = this.getUserMongoModel().findByFacebookId(facebookId);
		if (userBean == null) {
			return;
		}
		if (!userBean.getFacebookToken().equals(facebookToken)) {
			this.getUserMongoModel().updateFacebookToken(facebookId, facebookToken);
			getLogger().debug("update success");
		}
	}

}
