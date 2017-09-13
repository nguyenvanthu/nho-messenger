package com.nho.admin.router;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;

public interface AdminProcessor {
	PuElement execute(PuObjectRO request);
}
