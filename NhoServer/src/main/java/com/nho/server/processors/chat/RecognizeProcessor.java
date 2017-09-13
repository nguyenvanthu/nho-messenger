package com.nho.server.processors.chat;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;
import com.nho.chat.router.impl.GetDataMsgChatBotProcessor;
import com.nho.chat.statics.ChannelCommand;
import com.nho.chat.statics.ChatField;
import com.nho.message.MessageType;
import com.nho.message.request.chat.RecognizeRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.entity.user.User;
import com.nho.server.processors.impl.AbstractNhoProcessor;
import com.nho.server.statics.HandlerCollection;

@NhoCommandProcessor(command = { MessageType.RECOGNIZE_REQUEST })
public class RecognizeProcessor extends AbstractNhoProcessor<RecognizeRequest> {

	@Override
	protected void process(RecognizeRequest request) throws Exception {
		User user = this.getUserManager().getUserBySessionId(request.getSessionId());
		if(user == null ){
			getLogger().debug("user not login ");
			return ;
		}
		List<String> msgDatas = getDataMsgChatWithBot(user.getUserName());
		getLogger().debug("size messages : "+msgDatas.size());
	}
	/**
	 * get message data user chat with boot for recognize 
	 * call to {@link GetDataMsgChatBotProcessor}
	 */
	private List<String> getDataMsgChatWithBot(String userName){
		List<String> dataMsgs = new ArrayList<>();
		PuObject data = new PuObject();
		data.setInteger(ChatField.COMMAND, ChannelCommand.GET_DATA_MSG_BOT.getCode());
		data.setString(ChatField.USER_NAME, userName);
		PuObject result = (PuObject) this.getContext().getApi().call(HandlerCollection.CHAT_SERVER, data);
		if(result.getInteger(ChatField.STATUS) == 0){
			PuArray array = result.getPuArray(ChatField.DATA_MESSAGE);
			for(PuValue value : array){
				dataMsgs.add(value.getString());
			}
		}
		return dataMsgs;
	}
}
