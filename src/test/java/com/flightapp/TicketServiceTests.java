package com.flightapp;

import com.flightapp.model.Booking;
import com.flightapp.repository.BookingRepository;
import com.flightapp.service.TicketService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    @DisplayName("Returns outbound booking when PNR matches outbound record")
    void returnsOutboundBooking() {
        Booking booking = new Booking();
        booking.setBookingId("B-1");
        booking.setPnrOutbound("PNR001");
        booking.setContactName("Test User");

        when(bookingRepository.findByPnrOutbound("PNR001")).thenReturn(Mono.just(booking));
        when(bookingRepository.findByPnrReturn(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.getTicketByPnr("PNR001"))
                .assertNext(found -> {
                    assertEquals("PNR001", found.getPnrOutbound());
                    assertEquals("Test User", found.getContactName());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Falls back to return PNR when outbound is missing")
    void fallsBackToReturnPnr() {
        Booking booking = new Booking();
        booking.setBookingId("B-RET");
        booking.setPnrReturn("RET123");
        booking.setContactEmail("ret@example.com");

        when(bookingRepository.findByPnrOutbound("RET123")).thenReturn(Mono.empty());
        when(bookingRepository.findByPnrReturn("RET123")).thenReturn(Mono.just(booking));

        StepVerifier.create(ticketService.getTicketByPnr("RET123"))
                .assertNext(found -> assertEquals("B-RET", found.getBookingId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Completes empty when no booking is found for PNR")
    void returnsEmptyWhenNotFound() {
        String pnr = "MIS123";
        when(bookingRepository.findByPnrOutbound(pnr)).thenReturn(Mono.empty());
        when(bookingRepository.findByPnrReturn(pnr)).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.getTicketByPnr(pnr))
                .verifyErrorMatches(ex -> ex.getClass().getSimpleName().equals("ResourceNotFoundException"));
    }

    @Test
    @DisplayName("Rejects invalid PNR length")
    void rejectsInvalidPnrLength() {
        StepVerifier.create(ticketService.getTicketByPnr("123"))
                .verifyErrorMatches(ex -> ex.getClass().getSimpleName().equals("ValidationException"));
    }

    @Test
    @DisplayName("Rejects non-alphanumeric PNR")
    void rejectsNonAlphanumericPnr() {
        StepVerifier.create(ticketService.getTicketByPnr("PNR$12"))
                .verifyErrorMatches(ex -> ex.getClass().getSimpleName().equals("ValidationException"));
    }

    @Test
    @DisplayName("Rejects empty PNR")
    void rejectsEmptyPnr() {
        StepVerifier.create(ticketService.getTicketByPnr(" "))
                .verifyErrorMatches(ex -> ex.getClass().getSimpleName().equals("ValidationException"));
    }
}
