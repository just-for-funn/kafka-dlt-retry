package com.accenture.envision.kafka.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/consumers")
public class ConsumerController {

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@GetMapping(path = "/pause")
	boolean pause() {
		this.eventPublisher.publishEvent(new KafkaDelayedConsumer.KafkaConsumerEvent(true));
		return true;
	}

	@GetMapping(path = "/resume")
	boolean resume() {
		this.eventPublisher.publishEvent(new KafkaDelayedConsumer.KafkaConsumerEvent(false));
		return true;
	}


}
