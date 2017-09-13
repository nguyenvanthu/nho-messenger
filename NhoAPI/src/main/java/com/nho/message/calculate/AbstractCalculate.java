package com.nho.message.calculate;

import com.nhb.common.BaseLoggable;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;

public abstract class AbstractCalculate extends BaseLoggable implements Calculateable{
	protected int getSize(PuValue value) {
		int size = 0;
		if(value.getType() != PuDataType.NULL){
			StringBuilder builder = new StringBuilder();
			builder.append(value);
			size = builder.toString().toCharArray().length * 2;
		}
		return size;
	}
}
