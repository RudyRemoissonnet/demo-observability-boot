package com.example.client;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ClientApplication {

	private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);
	@Value("${app.boot3.host:localhost:8081}")
	private String appBoot3Host;
	@Value("${app.boot2.host:localhost:8082}")
	private String appBoot2Host;
	@Value("${app.server.host:localhost:7654}")
	private String appServerHost;

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	// tag::rest-template[]
	// IMPORTANT! To instrument RestTemplate you must inject the RestTemplateBuilder
	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	// end::rest-template[]

	// tag::runner[]
	@Bean
	CommandLineRunner myCommandLineRunner(ObservationRegistry registry, RestTemplate restTemplate) {
		Random highCardinalityValues = new Random(); // Simulates potentially large number of values
		List<String> lowCardinalityValues = Arrays.asList("userType1", "userType2", "userType3"); // Simulates low number of values
		return args -> {
			String highCardinalityUserId = String.valueOf(highCardinalityValues.nextLong(100_000));
			// Example of using the Observability API manually
			// <my.observation> is a "technical" name that does not depend on the context. It will be used to name e.g. Metrics
			 Observation.createNotStarted("my.observation", registry)
					 // Low cardinality means that the number of potential values won't be big. Low cardinality entries will end up in e.g. Metrics
					.lowCardinalityKeyValue("userType", randomUserTypePicker(lowCardinalityValues))
					 // High cardinality means that the number of potential values can be large. High cardinality entries will end up in e.g. Spans
					.highCardinalityKeyValue("userId", highCardinalityUserId)
					 // <command-line-runner> is a "contextual" name that gives more details within the provided context. It will be used to name e.g. Spans
					.contextualName("command-line-runner")
					 // The following lambda will be executed with an observation scope (e.g. all the MDC entries will be populated with tracing information). Also the observation will be started, stopped and if an error occurred it will be recorded on the observation
					.observe(() -> {

						try {
							log.info("Will send a request to the server"); // Since we're in an observation scope - this log line will contain tracing MDC entries ...
							String response = restTemplate.getForObject("http://" + appServerHost + "/user/{userId}", String.class, highCardinalityUserId); // Boot's RestTemplate instrumentation creates a child span here
							log.info("Got response [{}]", response); // ... so will this line
						} catch (Exception e) {
							log.error("app-server error", e);
						}

						callAppBoot3(restTemplate);
						callAppBoot2(restTemplate);
					});

		};
	}
	// end::runner[]

	private void callAppBoot3(RestTemplate restTemplate) {
		try {
			log.info("call boot-3 flight service");
			String flights = restTemplate.getForObject("http://" + appBoot3Host + "/flights", String.class);
			log.info("boot-3 flight service response: {}", flights);
		} catch (Exception e) {
			log.error("boot-3 service error", e);
		}
	}

	private void callAppBoot2(RestTemplate restTemplate) {
		try {
			log.info("call boot-2 application");
			String sb2response = restTemplate.getForObject("http://" + appBoot2Host + "/sb2", String.class);
			log.info("boot-2 response: {}", sb2response);
		} catch (Exception e) {
			log.error("boot-2 service error", e);
		}
	}

	Random randomUserTypePicker = new Random();

	private String randomUserTypePicker(List<String> lowNumberOfValues) {
		return lowNumberOfValues.get(randomUserTypePicker.nextInt(2));
	}
}
