package com.flightBooking.controller;

import com.flightBooking.model.Airline;
import com.flightBooking.request.AddAirlineRequest;
import com.flightBooking.service.AirlineInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import reactor.test.StepVerifier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class AirlineControllerTests {

    private AirlineInventoryService airlineService;
    private AirlineController controller;

    @BeforeEach
    void setup() throws Exception {
        airlineService = Mockito.mock(AirlineInventoryService.class);
        controller = new AirlineController();
        var f = AirlineController.class.getDeclaredField("airlineService");
        f.setAccessible(true);
        f.set(controller, airlineService);
    }

    private Airline airline(String code) {
        Airline airline = new Airline();
        airline.setAirlineCode(code);
        airline.setAirlineName("Airline " + code);
        return airline;
    }

    @Test
    @DisplayName("Add airline returns created airline")
    void addAirline() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setAirlineCode("AI");
        req.setAirlineName("Air India");
        Mockito.when(airlineService.addAirline(Mockito.any(AddAirlineRequest.class)))
                .thenReturn(Mono.just(airline("AI")));

        StepVerifier.create(controller.addAirline(req))
                .expectNextMatches(a -> "AI".equals(a.getAirlineCode()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get all airlines returns list")
    void getAllAirlines() {
        Mockito.when(airlineService.getAllAirlines())
                .thenReturn(Flux.just(airline("A1"), airline("A2")));

        StepVerifier.create(controller.getAllAirlines())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Get airline by code returns airline")
    void getAirline() {
        Mockito.when(airlineService.getAirline("AI")).thenReturn(Mono.just(airline("AI")));

        StepVerifier.create(controller.getAirline("AI"))
                .expectNextMatches(a -> "Airline AI".equals(a.getAirlineName()))
                .verifyComplete();
    }
}
