package com.accenture.envision.kafka.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/consumers")
public class ConsumerController {

	@GetMapping(path = "/pause")
	boolean pause() {
		return false;
	}

	@GetMapping(path = "/resume")
	boolean resume() {
		return true;
	}


}
