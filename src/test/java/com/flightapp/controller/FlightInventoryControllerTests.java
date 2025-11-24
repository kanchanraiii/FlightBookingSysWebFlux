package com.flightapp.controller;

import com.flightapp.controller.FlightInventoryController;
import com.flightapp.model.Cities;
import com.flightapp.model.FlightInventory;
import com.flightapp.request.AddFlightInventoryRequest;
import com.flightapp.service.FlightInventoryService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

class FlightInventoryControllerTests {

    private FlightInventoryService flightInventoryService;
    private FlightInventoryController controller;

    @BeforeEach
    void setup() throws Exception {
        flightInventoryService = Mockito.mock(FlightInventoryService.class);
        controller = new FlightInventoryController();
        var f = FlightInventoryController.class.getDeclaredField("FlightInventoryService");
        f.setAccessible(true);
        f.set(controller, flightInventoryService);
    }

    @Test
    @DisplayName("Add flight inventory returns created flight")
    void addInventory() {
        AddFlightInventoryRequest req = new AddFlightInventoryRequest();
        req.setAirlineCode("AI");
        req.setFlightNumber("AI101");
        req.setSourceCity(Cities.KANPUR);
        req.setDestinationCity(Cities.MUMBAI);
        req.setDepartureDate(LocalDate.now().plusDays(1));
        req.setArrivalDate(LocalDate.now().plusDays(1));
        req.setDepartureTime(LocalTime.of(10, 0));
        req.setArrivalTime(LocalTime.of(12, 0));
        req.setTotalSeats(100);
        req.setPrice(5000f);

        FlightInventory saved = new FlightInventory();
        saved.setFlightId("FL1");
        saved.setFlightNumber(req.getFlightNumber());
        Mockito.when(flightInventoryService.addInventory(Mockito.any(AddFlightInventoryRequest.class)))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(controller.addInventory(req))
                .expectNextMatches(f -> "AI101".equals(f.getFlightNumber()))
                .verifyComplete();
    }
}
