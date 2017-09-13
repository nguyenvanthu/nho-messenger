package com.nho.friend.router;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.friend.exception.FriendException;

public interface FriendProcessor{
	PuElement execute(PuObjectRO request) throws FriendException;
}
