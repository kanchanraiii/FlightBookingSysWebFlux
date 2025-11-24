package com.flightBooking.controller;

import com.flightBooking.model.Cities;
import com.flightBooking.model.FlightInventory;
import com.flightBooking.request.FlightSearchRequest;
import com.flightBooking.service.FlightSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

class FlightSearchControllerTests {

    private FlightSearchService searchService;
    private FlightSearchController controller;

    @BeforeEach
    void setup() throws Exception {
        searchService = Mockito.mock(FlightSearchService.class);
        controller = new FlightSearchController();
        var f = FlightSearchController.class.getDeclaredField("searchService");
        f.setAccessible(true);
        f.set(controller, searchService);
    }

    @Test
    @DisplayName("Search flights returns flight inventory")
    void searchFlights() {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setSourceCity(Cities.KANPUR);
        req.setDestinationCity(Cities.MUMBAI);
        req.setTravelDate(LocalDate.now().plusDays(1));

        FlightInventory inv = new FlightInventory();
        inv.setFlightId("FL1");
        inv.setDepartureTime(LocalTime.NOON);
        Mockito.when(searchService.searchFlights(Mockito.any(FlightSearchRequest.class)))
                .thenReturn(Flux.just(inv));

        StepVerifier.create(controller.searchFlights(req))
                .expectNextMatches(f -> "FL1".equals(f.getFlightId()))
                .verifyComplete();
    }
}
