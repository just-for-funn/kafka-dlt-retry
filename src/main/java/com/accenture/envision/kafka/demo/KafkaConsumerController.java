package com.accenture.envision.kafka.demo;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
public class KafkaConsumerController {
	private final Properties props;
	private ExecutorService executor;
	private Set<TopicPartition> assignMents;
	private AtomicBoolean closed = new AtomicBoolean(false);
	private KafkaConsumer<String, String> consumer;

	public KafkaConsumerController(Properties props) {
		this.props = props;
	}

	public void start() {
		this.closed.set(false);
		if (executor != null) {
			this.executor.shutdown();
		}
		executor = Executors.newSingleThreadExecutor();
		executor.submit(this::run);
	}

	private void run() {
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("test.DLT"));
		this.assignMents = consumer.assignment();
		try {
			while (!closed.get()) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
				records.forEach(o -> log.info("kafka consumer received: {} - {}", o.key(), o.value()));
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			log.info("Closing consumer");
			consumer.close();
		}
	}


	public void pause() {
		this.closed.set(true);
	}

	public void resume() {
		if (this.closed.get()) {
			log.info("Restarting consumer");
			this.start();
		}
	}

	public void stop() {
		this.closed.set(true);
	}
}
