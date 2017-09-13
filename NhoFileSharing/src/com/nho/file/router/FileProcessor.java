package com.nho.file.router;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;
import com.nho.file.exception.FileException;

public interface FileProcessor {
	PuElement execute(PuObjectRO request) throws FileException;
}
