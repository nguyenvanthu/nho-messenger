package com.nho.uams.client;

import java.util.UUID;

import com.nhb.common.utils.Converter;
import com.nhb.messaging.kafka.producer.KafkaMessageProducer;
import com.nho.uams.message.UAMSAbstractMessage;
import com.nho.uams.message.UAMSMessage;

public class UAMSClient {
	private String applicationId;
	private KafkaMessageProducer kafkaProducer;

	public UAMSClient(String applicationId, KafkaMessageProducer producer) {
		this.applicationId = applicationId;
		this.kafkaProducer = producer;
	}

	public void send(UAMSMessage message) {
		if (message instanceof UAMSAbstractMessage) {
			((UAMSAbstractMessage) message).setApplicationId(applicationId);
		}

		byte[] autoMessageId = Converter.uuidToBytes(UUID.randomUUID());
		this.kafkaProducer.send(autoMessageId, message.serialize());
	}
}
