package com.flightBooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import com.flightBooking.model.Booking;
import com.flightBooking.service.BookingService;

@RestController
@RequestMapping("/api/flight")
public class TicketController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/ticket/{pnr}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Booking> getTicket(@PathVariable String pnr) {
        return bookingService.getTicket(pnr);
    }

    @GetMapping("/booking/history/{email}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Booking> getHistory(@PathVariable String email) {
        return bookingService.getHistory(email);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> cancelTicket(@PathVariable String pnr) {
        return bookingService.cancelTicket(pnr)
                .thenReturn("Booking cancelled successfully");
    }
}
