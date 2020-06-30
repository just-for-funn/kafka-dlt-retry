package com.accenture.envision.kafka.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleConsumer {
	Map<String, AtomicInteger> counters = new HashMap<>();

	@KafkaListener(topics = "test", groupId = "test")
	public void onMessage(String key) {
		if (key.startsWith("fail")) {
			throw new RuntimeException("Will fail for: " + key);
		}
		log.info("message received {}", key);
	}

	@KafkaListener(id = "dltGroup", topics = "test.DLT")
	public void dltListen(String in) {
		log.info("Received from DLT: " + in);
	}
}
