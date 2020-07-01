package com.accenture.envision.kafka.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class AppConfiguration {

	@Bean
	public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory(
		ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
		ConsumerFactory<Object, Object> kafkaConsumerFactory,
		KafkaTemplate<Object, Object> template) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory);
		factory.setErrorHandler(errorHandler(template));
		return factory;
	}

	@Autowired
	KafkaOperations<Object, Object> template;

	SeekToCurrentErrorHandler errorHandler(KafkaOperations template) {
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
		return new SeekToCurrentErrorHandler(recoverer, new FixedBackOff(500L, 0L));
	}
}
