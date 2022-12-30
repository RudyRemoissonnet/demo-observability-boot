package com.example.flightsb2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@SpringBootApplication
public class Boot2Application {

    public static void main(String[] args) {
        SpringApplication.run(Boot2Application.class, args);
    }

}

@RestController
class MyController {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Random random = new Random();
    private final Tracer tracer;

    MyController(Tracer tracer) {
        this.tracer = tracer;
    }

    @GetMapping("sb2")
    public String sb2() throws InterruptedException {
        log.info("get request");

        Span newSpan = tracer.nextSpan().name("sb2 service");

        try (var ignored = tracer.withSpan(newSpan.start())) {

            newSpan.tag("sb2.tag",  "myTag");
            newSpan.event("start sb2Event");

            Thread.sleep(random.nextLong(100));

            return "ok !";

        } finally {
            newSpan.event("end sb2 event");
            newSpan.end();
        }
    }
}
