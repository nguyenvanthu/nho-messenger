package com.nho.uams.message;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;

public class UAMSMessageHelper {

	public static UAMSMessage deserialize(PuElement request) throws Exception {
		if (request instanceof PuArray) {
			PuArray array = (PuArray) request;
			int typeId = array.remove(0).getInteger();
			String applicationId = array.remove(0).getString();

			UAMSMessageType type = UAMSMessageType.fromCode(typeId);
			if (type == null) {
				throw new RuntimeException("typeId not found for " + typeId);
			}
			UAMSMessage message = type.getMessageClass().newInstance();
			if (message instanceof UAMSAbstractMessage) {
				((UAMSAbstractMessage) message).setApplicationId(applicationId);
				((UAMSAbstractMessage) message).readPuArray(array);
			}
			return message;
		} else if (request instanceof PuObjectRO) {
			PuObjectRO puo = (PuObjectRO) request;
			if (puo.variableExists("command") && puo.variableExists("applicationId")) {
				String command = puo.getString("command");
				String applicationId = puo.getString("applicationId");

				UAMSMessageType type = UAMSMessageType.forName(command);
				if (type == null) {
					throw new RuntimeException("type name not found for " + command);
				}
				UAMSMessage message = type.getMessageClass().newInstance();
				if (message instanceof UAMSAbstractMessage) {
					((UAMSAbstractMessage) message)
							.setApplicationId(applicationId);
					((UAMSAbstractMessage) message).readPuObject(puo);
				}
				return message;
			}
			throw new RuntimeException("PuObject request not contains command and applicationId " + puo);
		}
		throw new UnsupportedOperationException("UAMS message unsupported type");
	}
}
