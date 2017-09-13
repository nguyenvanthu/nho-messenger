package com.nho.uams.message;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuObjectRO;

public abstract class UAMSAbstractMessage implements UAMSMessage {
	private static final long serialVersionUID = 1L;
	private UAMSMessageType type;
	private String applicationId;

	@Override
	public PuArray serialize() {
		PuArray array = new PuArrayList();
		array.addFrom(type.getId());
		array.addFrom(applicationId);
		writePuArray(array);
		return array;
	}

	public static final <T extends UAMSAbstractMessage> T deserialize(PuArray puArray) {
		UAMSMessageType type = UAMSMessageType.fromCode(puArray.remove(0).getInteger());
		if (type != null) {
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) type.getMessageClass();
			T message = null;
			try {
				message = clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Error while creating new message Object", e);
			}
			if (message != null) {
				message.readPuArray(puArray);
			}
			return message;
		}
		return null;
	}
	
	protected abstract void writePuArray(PuArray array);

	public abstract void readPuArray(PuArray array);
	
	public void readPuObject(PuObjectRO puObject) {
		
	}

	public UAMSMessageType getType() {
		return type;
	}

	public void setType(UAMSMessageType type) {
		this.type = type;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

}
