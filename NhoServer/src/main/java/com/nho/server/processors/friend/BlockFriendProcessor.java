package com.nho.server.processors.friend;

import java.util.concurrent.ExecutionException;

import com.nhb.common.async.RPCFuture;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nho.friend.statics.FriendCommand;
import com.nho.friend.statics.FriendField;
import com.nho.message.MessageType;
import com.nho.message.request.friend.BlockFriendRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.user.User;
import com.nho.server.exception.FriendException;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.statics.BlockFriendResponse;
import com.nho.statics.Error;

/**
 * process request {@link BlockFriendRequest}
 * return response {@link BlockFriendResponse}
 * send command {@link FriendCommand#BLOCK_FRIEND} to {@link com.nho.friend.router.impl.BlockFriendProcessor}
 */
@NhoCommandProcessor(command={MessageType.BLOCK_FRIEND})
public class BlockFriendProcessor extends AbstractNhoProcessor<BlockFriendRequest> {

	@Override
	protected void process(BlockFriendRequest request) {
		try{
			User user = getUserManager().getUserBySessionId(request.getSessionId());
			BlockFriendResponse response = new BlockFriendResponse();
			if (user == null) {
				getLogger().debug("error user not loggin in BlockFriendProcessor");
				response.setSuccessful(false);
				response.setError(Error.USER_NOT_LOGGED_IN);
				this.send(response, request.getSessionId());
				return;
			}
			String senderUserName = request.getSenderUserName();
			String blockerUserName = request.getBlockerUserName();
			getLogger().debug("user {} block friend request from user {}", blockerUserName,senderUserName);
			PuObject data = new PuObject();
			data.setInteger(FriendField.COMMAND, FriendCommand.BLOCK_FRIEND.getCode());
			data.setString(FriendField.SENDER_NAME, senderUserName);
			data.setString(FriendField.BLOCKER_NAME, blockerUserName);
			data.setInteger(FriendField.STATUS, request.getStatusFriend().ordinal());
			
			RPCFuture<PuElement> publish = getContext().getFriendProducer().publish(data);
			try {
				PuElement puElement = publish.get();
				PuObject result = (PuObject) puElement;
				int status = result.getInteger(FriendField.STATUS);
				if(status == 0){
					UserMongoBean senderBean = this.getUserMongoModel().findByUserName(senderUserName);
					UserMongoBean blockerBean = this.getUserMongoModel().findByUserName(blockerUserName);
					response.setSuccessful(true);
					response.setSenderDisplayName(senderBean.getDisplayName());
					response.setSenderUserName(request.getSenderUserName());
					response.setAvatarSenderName(senderBean.getAvatar().getName());
					response.setStatus(request.getStatusFriend());
					
					response.setBlockerDisplayName(blockerBean.getDisplayName());
					response.setBlockerUserName(blockerBean.getUserName());
					response.setAvatarBlockerName(blockerBean.getAvatar().getName());
					
					this.sendToUser(response, user);
					this.sendToUserName(response, request.getSenderUserName());
				}else {
					response.setSuccessful(false);
					response.setError(Error.fromCode(result.getInteger(FriendField.ERROR)));
					this.send(response, request.getSessionId());
					return;
				}
			}catch(InterruptedException | ExecutionException e){
				getLogger().debug("error when using rabbit mq get data from FrienServer ", e);
				response.setSuccessful(false);
				response.setError(Error.ERROR_RABBIT_MQ);
				this.send(response, request.getSessionId());
				return;
			}
		}catch(Exception exception){
			throw new FriendException(exception.getMessage(), exception.getCause(),this.getContext());
		}
		
	}

//	private boolean isUserExist(String userName) {
//		boolean isExist = false;
//		UserMongoDBBean user = this.getUserMongoModel().findByUserName(userName);
//		if (user != null) {
//			getLogger().debug("user exist in db");
//			isExist = true;
//			return isExist;
//		}
//		return isExist;
//	}

	
}
