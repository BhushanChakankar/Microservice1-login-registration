//package com.extwebtech.kafka;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RegistrationProducer {
//
//	private NewTopic topic;
//
//	private KafkaTemplate<String, KafkaEvent> kafkaTemplate;
//
//	public RegistrationProducer(NewTopic topic, KafkaTemplate<String, KafkaEvent> kafkaTemplate) {
//		super();
//		this.topic = topic;
//		this.kafkaTemplate = kafkaTemplate;
//	}
//
//	public void sendMessage(KafkaEvent event) {
//
//		System.out.println(event);
//		Message<KafkaEvent> message = MessageBuilder.withPayload(event).setHeader(KafkaHeaders.TOPIC, topic.name())
//				.build();
//
//		kafkaTemplate.send(message);
//	}
//
//}
