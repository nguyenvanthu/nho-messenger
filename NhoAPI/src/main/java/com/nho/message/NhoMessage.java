package com.nho.message;

import com.nhb.common.annotations.Transparent;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nho.message.calculate.AbstractCalculate;

public abstract class NhoMessage extends AbstractCalculate{

	private MessageType type;
	private int messageId;
	protected String sessionId;

	@Transparent
	public MessageType getType() {
		return type;
	}

	protected final void setType(MessageType type) {
		this.type = type;
	}

	public final PuArray serialize() {
		PuArray result = new PuArrayList();
		result.addFrom(this.type.getId());
		result.addFrom(this.getMessageId());
		this.writePuArray(result);
		return result;
	}

	public static final <T extends NhoMessage> T deserialize(PuArray puArray, String sessionId) {
		MessageType type = MessageType.fromId(puArray.remove(0).getInteger());
		int messageId = puArray.remove(0).getInteger();
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
			message.setMessageId(messageId);
			message.sessionId = sessionId;
			return message;
		}
		return null;
	}
	

	protected void writePuArray(PuArray puArray) {
		// do nothing
	}

	protected void readPuArray(PuArray puArray) {
		// do nothing
	}
	public int getSize() {
		int size = 0;
		PuArray array = this.serialize();
		for(int i=0;i<array.size();i++){
			size += super.getSize(array.get(i));
		}
		return size;
	}
	
//	public int getSize(){
//		int size = 0;
//		PuArray array = this.serialize();
////		size +=array.toBytes().length;
//		for(int i = 0;i< array.size();i++){
//			size += array.get(i).toBytes().length;
//		}
//		return size;
//	}
	public String getSessionId() {
		return sessionId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
}
