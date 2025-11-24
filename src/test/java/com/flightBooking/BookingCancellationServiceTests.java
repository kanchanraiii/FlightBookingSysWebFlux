package com.flightBooking;

import com.flightBooking.exceptions.ResourceNotFoundException;
import com.flightBooking.exceptions.ValidationException;
import com.flightBooking.model.Booking;
import com.flightBooking.model.BookingStatus;
import com.flightBooking.model.FlightInventory;
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

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingCancellationServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    private FlightInventory flightDepartingInHours(long hoursAhead, int availableSeats) {
        FlightInventory flight = new FlightInventory();
        flight.setFlightId("FL-OUT");
        flight.setDepartureDate(LocalDate.now().plusDays(hoursAhead / 24));
        flight.setDepartureTime(LocalTime.now().plusHours(hoursAhead % 24));
        flight.setAvailableSeats(availableSeats);
        flight.setTotalSeats(availableSeats);
        return flight;
    }

    @Test
    @DisplayName("Cancel a confirmed booking more than 24 hours out restores seats")
    void cancelConfirmedBooking() {
        String pnr = "CANCEL1";
        Booking booking = new Booking();
        booking.setOutboundFlightId("FL-OUT");
        booking.setReturnFlight("FL-RET");
        booking.setPnrOutbound(pnr);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPassengers(2);

        FlightInventory outbound = flightDepartingInHours(48, 5);
        FlightInventory returning = flightDepartingInHours(48, 4);
        returning.setFlightId("FL-RET");

        when(bookingRepository.findByPnrOutbound(pnr)).thenReturn(Mono.just(booking));
        when(inventoryRepository.findById("FL-OUT")).thenReturn(Mono.just(outbound));
        when(inventoryRepository.findById("FL-RET")).thenReturn(Mono.just(returning));
        when(inventoryRepository.save(any(FlightInventory.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(bookingService.cancelTicket(pnr))
                .verifyComplete();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(7, outbound.getAvailableSeats());
        assertEquals(6, returning.getAvailableSeats());
    }

    @Test
    @DisplayName("Reject cancellation within 24 hours of departure")
    void rejectCancellationWithin24Hours() {
        String pnr = "CANCEL_SOON";
        Booking booking = new Booking();
        booking.setOutboundFlightId("FL-OUT");
        booking.setPnrOutbound(pnr);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPassengers(1);

        FlightInventory outbound = flightDepartingInHours(2, 5);

        when(bookingRepository.findByPnrOutbound(pnr)).thenReturn(Mono.just(booking));
        when(inventoryRepository.findById("FL-OUT")).thenReturn(Mono.just(outbound));

        StepVerifier.create(bookingService.cancelTicket(pnr))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject cancellation when booking is not found")
    void cancellationMissingBooking() {
        when(bookingRepository.findByPnrOutbound(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.cancelTicket("UNKNOWN"))
                .verifyError(ResourceNotFoundException.class);
    }
}
