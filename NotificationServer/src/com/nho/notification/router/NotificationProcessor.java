package com.nho.notification.router;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.notification.exception.NotificationException;

public interface NotificationProcessor {
	PuElement execute(PuObjectRO request) throws NotificationException;
}
