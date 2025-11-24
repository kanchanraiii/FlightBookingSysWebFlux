package com.flightapp;

import com.flightapp.model.Booking;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.service.BookingService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingHistoryServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Get booking history for a user with multiple bookings")
    void getHistoryForUserWithMultipleBookings() {
        String email = "user@example.com";

        Booking b1 = new Booking();
        b1.setContactEmail(email);
        Booking b2 = new Booking();
        b2.setContactEmail(email);

        when(bookingRepository.findByContactEmail(email))
                .thenReturn(Flux.just(b1, b2));

        StepVerifier.create(bookingService.getHistory(email))
                .expectNext(b1)
                .expectNext(b2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Get booking history returns empty when no bookings exist")
    void historyCompletesEmptyWhenNoBookings() {
        when(bookingRepository.findByContactEmail(anyString()))
                .thenReturn(Flux.empty());

        StepVerifier.create(bookingService.getHistory("none@example.com"))
                .verifyComplete();
    }
}
