package com.nho.uams.data;

import com.fasterxml.uuid.Generators;
import com.nhb.common.db.beans.UUIDBean;

public class TimeBasedUUIDBean extends UUIDBean {
	private static final long serialVersionUID = 4772393814609038799L;

	@Override
	public void autoId() {
		setId(Generators.timeBasedGenerator().generate());
	}
}
