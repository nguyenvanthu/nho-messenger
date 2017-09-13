package com.nho.uams.router;

import com.nhb.common.data.PuElement;
import com.nho.uams.message.UAMSMessage;

public interface UAMSProcessor {
	PuElement execute(UAMSMessage request);
}
