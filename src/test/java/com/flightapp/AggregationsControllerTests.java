package com.flightapp;

import com.flightapp.controller.AggregationsController;
import com.flightapp.service.AggregationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class AggregationsControllerTests {

    private AggregationsService service;
    private AggregationsController controller;

    @BeforeEach
    void setup() throws Exception {
        service = Mockito.mock(AggregationsService.class);
        controller = new AggregationsController();
        var f = AggregationsController.class.getDeclaredField("service");
        f.setAccessible(true);
        f.set(controller, service);
    }

    @Test
    void testAvgPriceEndpoint() {
        Mockito.when(service.avgPricePerRoute()).thenReturn(Flux.empty());
        StepVerifier.create(controller.avgPricePerRoute()).verifyComplete();
    }

    @Test
    void testFlightsPerAirlineEndpoint() {
        Mockito.when(service.flightsPerAirline()).thenReturn(Flux.empty());
        StepVerifier.create(controller.flightsPerAirline()).verifyComplete();
    }

    @Test
    void testSeatStatsEndpoint() {
        Mockito.when(service.seatsPerAirline()).thenReturn(Flux.empty());
        StepVerifier.create(controller.seatsPerAirline()).verifyComplete();
    }

    @Test
    void testUpcomingFlightsEndpoint() {
        Mockito.when(service.upcomingFlightsPerDay()).thenReturn(Flux.empty());
        StepVerifier.create(controller.upcomingFlights()).verifyComplete();
    }

    @Test
    void testMealStatsEndpoint() {
        Mockito.when(service.mealStats()).thenReturn(Flux.empty());
        StepVerifier.create(controller.mealStats()).verifyComplete();
    }

    @Test
    void testExpensiveFlightsEndpoint() {
        Mockito.when(service.expensiveFlights()).thenReturn(Flux.empty());
        StepVerifier.create(controller.expensiveFlights()).verifyComplete();
    }
}
