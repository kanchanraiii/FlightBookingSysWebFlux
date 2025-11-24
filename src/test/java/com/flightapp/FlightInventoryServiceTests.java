package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Airline;
import com.flightapp.model.Cities;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.Seat;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.SeatsRepository;
import com.flightapp.request.AddFlightInventoryRequest;
import com.flightapp.service.FlightInventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightInventoryServiceTests {

    @Mock
    private FlightInventoryRepository flightInventoryRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private SeatsRepository seatsRepository;

    @InjectMocks
    private FlightInventoryService flightInventoryService;

    private AddFlightInventoryRequest validRequest() {
        AddFlightInventoryRequest req = new AddFlightInventoryRequest();
        req.setAirlineCode("AI");
        req.setFlightNumber("AI101");
        req.setSourceCity(Cities.KANPUR);
        req.setDestinationCity(Cities.MUMBAI);
        req.setDepartureDate(LocalDate.now().plusDays(10));
        req.setArrivalDate(req.getDepartureDate().plusDays(1));
        req.setDepartureTime(LocalTime.of(9, 0));
        req.setArrivalTime(LocalTime.of(11, 0));
        req.setTotalSeats(5);
        req.setPrice(4500.0f);
        req.setMealAvailable(true);
        return req;
    }

    private void mockAirlineLookup(String code) {
        Airline airline = new Airline();
        airline.setAirlineCode(code);
        airline.setAirlineName("Test Airline");
        when(airlineRepository.findById(code)).thenReturn(Mono.just(airline));
    }

    @SuppressWarnings("unchecked")
	private void mockSeatGeneration() {
        when(seatsRepository.saveAll(any(Iterable.class)))
                .thenAnswer(inv -> Flux.fromIterable((Iterable<Seat>) inv.getArgument(0)));
    }

    private void mockSaveInventory() {
        when(flightInventoryRepository.save(any(FlightInventory.class)))
                .thenAnswer(invocation -> {
                    FlightInventory inv = invocation.getArgument(0);
                    inv.setFlightId("FL-" + inv.getFlightNumber());
                    return Mono.just(inv);
                });
    }

    @Test
    @DisplayName("Add inventory copies fields, seats, and meal flag")
    void addInventoryWhenAirlineExists() {
        AddFlightInventoryRequest req = validRequest();
        mockAirlineLookup(req.getAirlineCode());
        when(flightInventoryRepository.findFirstByFlightNumberAndDepartureDate(
                req.getFlightNumber(), req.getDepartureDate())).thenReturn(Mono.empty());
        mockSaveInventory();
        mockSeatGeneration();

        StepVerifier.create(flightInventoryService.addInventory(req))
                .assertNext(inv -> {
                    assertEquals(req.getFlightNumber(), inv.getFlightNumber());
                    assertEquals(req.getAirlineCode(), inv.getAirlineCode());
                    assertEquals(req.getTotalSeats().intValue(), inv.getAvailableSeats());
                    assertEquals(req.getPrice().doubleValue(), inv.getPrice(), 0.001);
                    assertTrue(inv.isMealAvailable());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Reject when airline is missing")
    void addInventoryWhenAirlineMissing() {
        AddFlightInventoryRequest req = validRequest();
        when(airlineRepository.findById(req.getAirlineCode()))
                .thenReturn(Mono.empty());

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Reject when source and destination are same")
    void rejectSameSourceDestination() {
        AddFlightInventoryRequest req = validRequest();
        req.setDestinationCity(req.getSourceCity());

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when flight number is missing")
    void rejectMissingFlightNumber() {
        AddFlightInventoryRequest req = validRequest();
        req.setFlightNumber(null);

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when arrival info is missing")
    void rejectMissingArrivalInfo() {
        AddFlightInventoryRequest req = validRequest();
        req.setArrivalDate(null);

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when departure is in the past")
    void rejectDepartureInPast() {
        AddFlightInventoryRequest req = validRequest();
        req.setDepartureDate(LocalDate.now().minusDays(1));
        req.setArrivalDate(req.getDepartureDate());

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when arrival is before departure")
    void rejectArrivalBeforeDeparture() {
        AddFlightInventoryRequest req = validRequest();
        req.setArrivalDate(req.getDepartureDate());
        req.setArrivalTime(req.getDepartureTime().minusHours(1));

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject when total seats or price are non-positive")
    void rejectInvalidSeatOrPrice() {
        AddFlightInventoryRequest req = validRequest();
        req.setTotalSeats(0);

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);

        req = validRequest();
        req.setPrice(0f);

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject duplicate flight number on same date")
    void rejectDuplicateFlight() {
        AddFlightInventoryRequest req = validRequest();
        mockAirlineLookup(req.getAirlineCode());
        when(flightInventoryRepository.findFirstByFlightNumberAndDepartureDate(
                req.getFlightNumber(), req.getDepartureDate()))
                .thenReturn(Mono.just(new FlightInventory()));

        StepVerifier.create(flightInventoryService.addInventory(req))
                .verifyError(ValidationException.class);
    }
}
