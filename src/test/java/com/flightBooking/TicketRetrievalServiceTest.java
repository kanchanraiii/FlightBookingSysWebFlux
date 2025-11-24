package com.flightBooking;

import com.flightBooking.model.Booking;
import com.flightBooking.model.BookingStatus;
import com.flightBooking.repository.BookingRepository;
import com.flightBooking.repository.FlightInventoryRepository;
import com.flightBooking.repository.PassengerRepository;
import com.flightBooking.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketRetrievalServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking bookingWithPnr(String pnr, String name) {
        Booking booking = new Booking();
        booking.setBookingId("B-" + pnr);
        booking.setPnrOutbound(pnr);
        booking.setContactName(name);
        booking.setContactEmail("user@example.com");
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    @Test
    @DisplayName("Retrieve ticket details using a valid existing PNR")
    void retrieveTicketWithValidPnr() {
        String pnr = "ABC123";
        Booking booking = bookingWithPnr(pnr, "Test User");
        when(bookingRepository.findByPnrOutbound(pnr)).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingService.getTicket(pnr))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(pnr, result.getPnrOutbound());
                    assertEquals("Test User", result.getContactName());
                    assertEquals("user@example.com", result.getContactEmail());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Completes empty when PNR does not exist")
    void completesEmptyWhenPnrMissing() {
        when(bookingRepository.findByPnrOutbound(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getTicket("NOEXIST"))
                .verifyErrorMatches(ex -> ex.getClass().getSimpleName().equals("ResourceNotFoundException"));
    }
}
