package com.nho.server.processors.friend;

import com.nho.message.MessageType;
import com.nho.message.request.friend.SearchFriendRequest;
import com.nho.server.annotation.NhoCommandProcessor;
import com.nho.server.processors.impl.AbstractNhoProcessor;
@NhoCommandProcessor(command={MessageType.SEARCH_FRIEND})
public class SearchFriendProcessor extends AbstractNhoProcessor<SearchFriendRequest> {

	@Override
	protected void process(SearchFriendRequest request) {
		
	}

}
