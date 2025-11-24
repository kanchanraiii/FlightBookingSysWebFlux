package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Cities;
import com.flightapp.model.FlightInventory;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.request.FlightSearchRequest;
import com.flightapp.service.FlightSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightSearchServiceTests {

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @InjectMocks
    private FlightSearchService flightSearchService;

    private FlightSearchRequest request(LocalDate travelDate) {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setSourceCity(Cities.KANPUR);
        req.setDestinationCity(Cities.MUMBAI);
        req.setTravelDate(travelDate);
        return req;
    }

    private FlightInventory sampleFlight(LocalDate departureDate) {
        FlightInventory inv = new FlightInventory();
        inv.setFlightId("FL-201");
        inv.setFlightNumber("AI201");
        inv.setSourceCity(Cities.KANPUR);
        inv.setDestinationCity(Cities.MUMBAI);
        inv.setDepartureDate(departureDate);
        inv.setDepartureTime(LocalTime.of(9, 0));
        inv.setPrice(4000.0);
        return inv;
    }

    @Test
    @DisplayName("Search flight with valid source, destination and date")
    void searchOneWayFlightWithValidDetails() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        FlightSearchRequest req = request(futureDate);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                req.getSourceCity(), req.getDestinationCity(), req.getTravelDate()))
                .thenReturn(Flux.just(sampleFlight(futureDate)));

        StepVerifier.create(flightSearchService.searchFlights(req))
                .assertNext(f -> assertEquals(futureDate, f.getDepartureDate()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Search results contain key fields")
    void searchResultsContainKeyFields() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        FlightSearchRequest req = request(futureDate);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                any(), any(), any())).thenReturn(Flux.just(sampleFlight(futureDate)));

        StepVerifier.create(flightSearchService.searchFlights(req))
                .assertNext(result -> {
                    assertNotNull(result.getDepartureDate());
                    assertNotNull(result.getDepartureTime());
                    assertNotNull(result.getPrice());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Error when no flights exist for route")
    void noFlightsForRouteThrowsNotFound() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        FlightSearchRequest req = request(futureDate);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                req.getSourceCity(), req.getDestinationCity(), req.getTravelDate()))
                .thenReturn(Flux.empty());

        StepVerifier.create(flightSearchService.searchFlights(req))
                .verifyError(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Reject when source and destination are the same")
    void rejectSameSourceDestination() {
        FlightSearchRequest req = request(LocalDate.now().plusDays(2));
        req.setDestinationCity(req.getSourceCity());

        StepVerifier.create(flightSearchService.searchFlights(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when destination city is missing")
    void rejectMissingDestination() {
        FlightSearchRequest req = request(LocalDate.now().plusDays(2));
        req.setDestinationCity(null);

        StepVerifier.create(flightSearchService.searchFlights(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when travel date is missing")
    void rejectMissingTravelDate() {
        FlightSearchRequest req = request(null);

        StepVerifier.create(flightSearchService.searchFlights(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when travel date is in the past")
    void rejectPastDate() {
        FlightSearchRequest req = request(LocalDate.now().minusDays(1));

        StepVerifier.create(flightSearchService.searchFlights(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when required fields are missing")
    void rejectMissingFields() {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setDestinationCity(Cities.MUMBAI);
        req.setTravelDate(LocalDate.now().plusDays(1));

        StepVerifier.create(flightSearchService.searchFlights(req))
                .verifyError(ValidationException.class);

        req = request(LocalDate.now().plusDays(1));
        req.setSourceCity(null);

        StepVerifier.create(flightSearchService.searchFlights(req))
                .verifyError(ValidationException.class);
    }
}
