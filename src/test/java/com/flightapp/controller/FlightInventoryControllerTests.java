package com.flightapp.controller;

import com.flightapp.model.FlightInventory;
import com.flightapp.request.AddFlightInventoryRequest;
import com.flightapp.service.FlightInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FlightInventoryControllerTests {

    private FlightInventoryService inventoryService;
    private FlightInventoryController controller;

    @BeforeEach
    void setup() throws Exception {
        inventoryService = Mockito.mock(FlightInventoryService.class);
        controller = new FlightInventoryController();
        var f = FlightInventoryController.class.getDeclaredField("FlightInventoryService");
        f.setAccessible(true);
        f.set(controller, inventoryService);
    }

    @Test
    @DisplayName("Add inventory delegates to service and returns saved flight")
    void addInventory() {
        FlightInventory inv = new FlightInventory();
        inv.setFlightId("FL-1");
        Mockito.when(inventoryService.addInventory(Mockito.any(AddFlightInventoryRequest.class)))
                .thenReturn(Mono.just(inv));

        StepVerifier.create(controller.addInventory(new AddFlightInventoryRequest()))
                .expectNextMatches(saved -> "FL-1".equals(saved.getFlightId()))
                .verifyComplete();
    }
}
