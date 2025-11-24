package com.flightapp.controller;

import com.flightapp.controller.BookingController;
import com.flightapp.model.Booking;
import com.flightapp.model.TripType;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;
import com.flightapp.service.BookingService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import reactor.test.StepVerifier;
import reactor.core.publisher.Mono;

import java.util.List;

class BookingControllerTests {

    private BookingService bookingService;
    private BookingController controller;

    @BeforeEach
    void setup() throws Exception {
        bookingService = Mockito.mock(BookingService.class);
        controller = new BookingController();
        var f = BookingController.class.getDeclaredField("bookingService");
        f.setAccessible(true);
        f.set(controller, bookingService);
    }

    @Test
    @DisplayName("Book flight returns booking")
    void bookFlight() {
        BookingRequest req = new BookingRequest();
        req.setContactName("Tester");
        req.setContactEmail("test@example.com");
        req.setTripType(TripType.ONE_WAY);
        PassengerRequest p = new PassengerRequest();
        p.setName("P1");
        p.setAge(30);
        p.setSeatOutbound("A1");
        req.setPassengers(List.of(p));

        Booking booking = new Booking();
        booking.setBookingId("B1");
        Mockito.when(bookingService.bookFlight(Mockito.eq("OUT1"), Mockito.any(BookingRequest.class)))
                .thenReturn(Mono.just(booking));

        StepVerifier.create(controller.bookFlight("OUT1", req))
                .expectNextMatches(b -> "B1".equals(b.getBookingId()))
                .verifyComplete();
    }
}
