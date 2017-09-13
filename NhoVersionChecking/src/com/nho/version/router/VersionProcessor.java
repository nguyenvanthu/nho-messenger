package com.nho.version.router;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;

public interface VersionProcessor {
	PuElement execute(PuObjectRO request);
}
