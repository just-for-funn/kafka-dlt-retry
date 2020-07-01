package com.accenture.envision.kafka.demo;

import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaDelayedConsumer {

	private KafkaConsumerController controller;

	@EventListener(ApplicationReadyEvent.class)
	public void onAppReady() {
		log.info("app init");
		startConsumer();
	}

	private void startConsumer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "suspendable");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		this.controller = new KafkaConsumerController(props);
		controller.start();
	}


	@PostConstruct
	void onPostContruct() {
		log.info("postcontruct");
	}

	@PreDestroy
	void onDestroy() {
		log.info("Closing consumer");
		this.controller.stop();
	}

	@EventListener
	void onConsumerEvent(KafkaConsumerEvent event) {
		log.info("Consumer event is received {}", event);
		if (event.isPause()) {
			log.info("Pausing");
			this.controller.pause();
		} else {
			log.info("resuming");
			this.controller.resume();
		}
	}

	@Data
	@AllArgsConstructor
	public static class KafkaConsumerEvent {
		boolean isPause;
	}

}
