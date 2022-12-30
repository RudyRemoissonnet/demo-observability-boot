package com.example.flight;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
public class FlightApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightApplication.class, args);
    }

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

}

@RestController
class FlightController {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    final FlightService flightService;
    final ObservationRegistry observationRegistry;

    FlightController(FlightService flightService,
                     ObservationRegistry observationRegistry) {
        this.flightService = flightService;
        this.observationRegistry = observationRegistry;
    }

    @GetMapping("/flights")
    List<FlightResponse> flights() {
        log.info("searching flights");

        List<Flight> flights = flightService.findAll();

        log.info("found {} flights", flights.size());

        return flights.stream()
                .map(f -> new FlightResponse(f.id, f.origin, f.destination))
                .toList();
    }

    @GetMapping("/flightsWithEvent")
    List<FlightResponse> flightsWithEvent() {
        log.info("searching flights with event");

        var observation = Observation.start("flight.search", observationRegistry);
        try (var ignored = observation.openScope()) {

            observation.contextualName("search flights");
            observation.event(Observation.Event.of("start searching for flights"));

            List<Flight> flights = flightService.findAll();

            log.info("found {} flights", flights.size());

            return flights.stream()
                    .map(f -> new FlightResponse(f.id, f.origin, f.destination))
                    .toList();

        } finally {
            observation.event(Observation.Event.of("end searching for flights"));
            observation.stop();
        }
    }
}

@Component
class FlightService {

    final FlightRepository flightRepository;

    FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    List<Flight> findAll() {
        return flightRepository.findAll();
    }
}

interface FlightRepository extends JpaRepository<Flight, Long> {

}

record FlightResponse(Long id, String origin, String destination) {

}

@Entity
class Flight {

    @Id
    @GeneratedValue
    Long id;
    String origin;
    String destination;
}


