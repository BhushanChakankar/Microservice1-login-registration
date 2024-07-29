package com.extwebtech.kafka;

import lombok.Data;

@Data

public class KafkaEvent {
	private int userId;
	private String topic;
	private String deviceToken;
}