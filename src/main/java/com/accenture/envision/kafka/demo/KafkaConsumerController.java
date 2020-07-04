package com.accenture.envision.kafka.demo;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;


@Slf4j
public class KafkaConsumerController {
	private final Properties props;
	private ExecutorService executor;
	private Set<TopicPartition> assignMents;
	private AtomicBoolean closed = new AtomicBoolean(false);
	AtomicReference<Boolean> paused = new AtomicReference<Boolean>(null);


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
				handlePause();
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
				records.forEach(this::printRecord);
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

	private void handlePause() {
		if (this.paused.get() == null) {
			return;
		}
		if (paused.get()) {
			log.info("pausing consumer");
			this.assignMents = this.consumer.assignment();
			this.consumer.pause(this.assignMents);
			this.paused.set(null);
			log.info("pause finished");
		} else {
			log.info("resuming consumers");
			this.consumer.resume(this.assignMents);
			this.paused.set(null);
			log.info("resuming consumers finished");
		}
	}

	private void printRecord(ConsumerRecord<String, String> record) {
		String headers = Arrays.stream(record.headers().toArray())
							   .filter(o -> !o.key().contains("exception"))
							   .map(h -> h.key() + stringify(h.value()))
							   .collect(Collectors.joining(System.lineSeparator()));
		log.info("partition={} offset={} key={} value={} headers={}", record.partition(), record.offset(), record.key(), record.value(),
			headers);
	}

	private String stringify(byte[] value) {
		return new String(value);
	}


	public void pause() {
		this.paused.set(true);
	}

	public void resume() {
		this.paused.set(false);
	}

	public void stop() {
		this.closed.set(true);
	}
}
