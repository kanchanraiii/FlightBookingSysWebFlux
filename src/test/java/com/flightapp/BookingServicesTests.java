package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.Gender;
import com.flightapp.model.Meal;
import com.flightapp.model.TripType;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;
import com.flightapp.service.BookingService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServicesTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository flightInventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    private FlightInventory flight(String id, int availableSeats) {
        FlightInventory flight = new FlightInventory();
        flight.setFlightId(id);
        flight.setFlightNumber("FL-" + id);
        flight.setDepartureDate(LocalDate.now().plusDays(5));
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalDate(LocalDate.now().plusDays(5));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setAvailableSeats(availableSeats);
        flight.setTotalSeats(availableSeats);
        return flight;
    }

    private BookingRequest bookingRequest(int passengerCount, boolean roundTrip) {
        BookingRequest req = new BookingRequest();
        req.setContactName("Test User");
        req.setContactEmail("user@example.com");
        req.setTripType(roundTrip ? TripType.ROUND_TRIP : TripType.ONE_WAY);
        if (roundTrip) {
            req.setReturnFlightId("RET1");
        }

        List<PassengerRequest> passengers = new ArrayList<>();
        for (int i = 0; i < passengerCount; i++) {
            PassengerRequest p = new PassengerRequest();
            p.setName("Passenger " + (i + 1));
            p.setAge(30);
            p.setGender(Gender.MALE);
            p.setSeatOutbound("A" + (i + 1));
            p.setSeatReturn("B" + (i + 1));
            p.setMeal(Meal.VEG);
            passengers.add(p);
        }
        req.setPassengers(passengers);
        return req;
    }

    private void mockPassengerSave() {
        when(passengerRepository.saveAll(any(Publisher.class)))
                .thenAnswer(inv -> Flux.from((Publisher<?>) inv.getArgument(0)));
    }

    @Test
    @DisplayName("Book a one-way flight and generate PNR, reducing seats")
    void bookOneWayGeneratesPnr() {
        BookingRequest req = bookingRequest(2, false);
        FlightInventory outbound = flight("OUT1", 5);

        when(flightInventoryRepository.findById("OUT1")).thenReturn(Mono.just(outbound));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(flightInventoryRepository.save(any(FlightInventory.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        mockPassengerSave();

        StepVerifier.create(bookingService.bookFlight("OUT1", req))
                .assertNext(booking -> {
                    assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
                    assertEquals(outbound.getFlightId(), booking.getOutboundFlightId());
                    assertEquals(2, booking.getTotalPassengers());
                    assertNotNull(booking.getPnrOutbound());
                    assertEquals(6, booking.getPnrOutbound().length());
                    assertEquals(3, outbound.getAvailableSeats());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Book a round trip reduces both flights and sets return id")
    void bookRoundTripSetsReturnFlight() {
        BookingRequest req = bookingRequest(1, true);
        FlightInventory outbound = flight("OUT1", 5);
        FlightInventory returning = flight("RET1", 4);

        when(flightInventoryRepository.findById("OUT1")).thenReturn(Mono.just(outbound));
        when(flightInventoryRepository.findById("RET1")).thenReturn(Mono.just(returning));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(flightInventoryRepository.save(any(FlightInventory.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        mockPassengerSave();

        StepVerifier.create(bookingService.bookFlight("OUT1", req))
                .assertNext(booking -> {
                    assertEquals("RET1", booking.getReturnFlight());
                    assertEquals(TripType.ROUND_TRIP, booking.getTripType());
                })
                .verifyComplete();

        assertEquals(4, outbound.getAvailableSeats());
        assertEquals(3, returning.getAvailableSeats());
    }

    @Test
    @DisplayName("Booking fails when outbound flight is missing")
    void bookingFailsWhenOutboundMissing() {
        BookingRequest req = bookingRequest(1, false);
        when(flightInventoryRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.bookFlight("OUT404", req))
                .verifyError(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Round trip booking fails when return flight is missing")
    void bookingFailsWhenReturnMissing() {
        BookingRequest req = bookingRequest(1, true);
        FlightInventory outbound = flight("OUT1", 3);

        when(flightInventoryRepository.findById("OUT1")).thenReturn(Mono.just(outbound));
        when(flightInventoryRepository.findById("RET1")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.bookFlight("OUT1", req))
                .verifyError(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Reject booking when not enough seats are available")
    void rejectWhenSeatsInsufficient() {
        BookingRequest req = bookingRequest(3, false);
        FlightInventory outbound = flight("OUT1", 2);

        when(flightInventoryRepository.findById("OUT1")).thenReturn(Mono.just(outbound));

        StepVerifier.create(bookingService.bookFlight("OUT1", req))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject booking when passengers are missing")
    void rejectWhenNoPassengers() {
        BookingRequest req = bookingRequest(0, false);
        req.setPassengers(new ArrayList<>());

        org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class,
                () -> bookingService.bookFlight("OUT1", req));
    }

    @Test
    @DisplayName("Reject booking when trip type is missing or invalid for round trip")
    void rejectWhenTripTypeInvalid() {
        BookingRequest reqMissingTripType = bookingRequest(1, false);
        reqMissingTripType.setTripType(null);

        org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class,
                () -> bookingService.bookFlight("OUT1", reqMissingTripType));

        BookingRequest reqMissingReturn = bookingRequest(1, true);
        reqMissingReturn.setReturnFlightId(null);

        org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class,
                () -> bookingService.bookFlight("OUT1", reqMissingReturn));
    }

    @Test
    @DisplayName("Reject booking when passenger age or seats are invalid")
    void rejectInvalidPassengerData() {
        BookingRequest req = bookingRequest(1, false);
        req.getPassengers().get(0).setAge(0);
        FlightInventory outbound = flight("OUT1", 5);
        when(flightInventoryRepository.findById("OUT1")).thenReturn(Mono.just(outbound));

        StepVerifier.create(bookingService.bookFlight("OUT1", req))
                .verifyError(ValidationException.class);

        req = bookingRequest(1, false);
        req.getPassengers().get(0).setSeatOutbound("  ");
        when(flightInventoryRepository.findById("OUT1")).thenReturn(Mono.just(flight("OUT1", 5)));

        StepVerifier.create(bookingService.bookFlight("OUT1", req))
                .verifyError(ValidationException.class);

        req = bookingRequest(1, true);
        req.getPassengers().get(0).setSeatReturn(null);
        when(flightInventoryRepository.findById("OUT1")).thenReturn(Mono.just(flight("OUT1", 5)));
        when(flightInventoryRepository.findById("RET1")).thenReturn(Mono.just(flight("RET1", 5)));

        StepVerifier.create(bookingService.bookFlight("OUT1", req))
                .verifyError(ValidationException.class);
    }
}
