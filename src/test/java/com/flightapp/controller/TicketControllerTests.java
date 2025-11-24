package com.flightapp.controller;

import com.flightapp.model.Booking;
import com.flightapp.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class TicketControllerTests {

    private BookingService bookingService;
    private TicketController controller;

    @BeforeEach
    void setup() throws Exception {
        bookingService = Mockito.mock(BookingService.class);
        controller = new TicketController();
        var f = TicketController.class.getDeclaredField("bookingService");
        f.setAccessible(true);
        f.set(controller, bookingService);
    }

    private Booking booking(String id) {
        Booking b = new Booking();
        b.setBookingId(id);
        b.setPnrOutbound("PNR123");
        return b;
    }

    @Test
    @DisplayName("Get ticket by PNR returns booking")
    void getTicket() {
        Mockito.when(bookingService.getTicket("PNR123")).thenReturn(Mono.just(booking("B1")));

        StepVerifier.create(controller.getTicket("PNR123"))
                .expectNextMatches(b -> "B1".equals(b.getBookingId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get booking history returns list")
    void getHistory() {
        Mockito.when(bookingService.getHistory("user@example.com"))
                .thenReturn(Flux.just(booking("B1"), booking("B2")));

        StepVerifier.create(controller.getHistory("user@example.com"))
                .expectNextMatches(b -> "B1".equals(b.getBookingId()))
                .expectNextMatches(b -> "B2".equals(b.getBookingId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Cancel booking returns confirmation message")
    void cancelBooking() {
        Mockito.when(bookingService.cancelTicket("PNR123")).thenReturn(Mono.empty());

        StepVerifier.create(controller.cancelTicket("PNR123"))
                .expectNext("Booking cancelled successfully")
                .verifyComplete();
    }
}
