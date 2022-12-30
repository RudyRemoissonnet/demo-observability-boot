package com.example.flight;

import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    @Mock
    private FlightService flightService;
    private TestObservationRegistry registry;
    private FlightController flightController;

    @BeforeEach
    void setup() {
        registry = TestObservationRegistry.create();
        flightController = new FlightController(flightService, registry);
    }

    @Test
    void flightWithEvent_shouldCreateContext() {

        when(flightService.findAll()).thenReturn(List.of(new Flight()));

        List<FlightResponse> flightsResponse = flightController.flightsWithEvent();
        assertThat(flightsResponse).hasSize(1);

        TestObservationRegistryAssert.assertThat(registry)
                .doesNotHaveAnyRemainingCurrentObservation()
                .hasObservationWithNameEqualTo("flight.search")
                .that()
                .hasBeenStarted()
                .hasBeenStopped();
    }
}