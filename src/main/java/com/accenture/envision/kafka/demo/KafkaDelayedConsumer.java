package com.accenture.envision.kafka.demo;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaDelayedConsumer {

	@Autowired
	ApplicationEventPublisher publisher;

	@EventListener(ApplicationReadyEvent.class)
	public void onAppReady() {
		log.info("app init");
	}

	@PostConstruct
	void onPostContruct() {
		log.info("postcontruct");
	}
}
