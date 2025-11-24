package com.flightapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import com.flightapp.model.Booking;
import com.flightapp.service.BookingService;

@RestController
@RequestMapping("/api/flight")
public class TicketController {

    @Autowired
    private BookingService bookingService;

    // to get ticket based on pnr
    @GetMapping("/ticket/{pnr}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Booking> getTicket(@PathVariable String pnr) {
        return bookingService.getTicket(pnr);
    }

    // to get booking history based on email
    @GetMapping("/booking/history/{email}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Booking> getHistory(@PathVariable String email) {
        return bookingService.getHistory(email);
    }

    // to cancel a ticket based on pnr
    @DeleteMapping("/booking/cancel/{pnr}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> cancelTicket(@PathVariable String pnr) {
        return bookingService.cancelTicket(pnr)
                .thenReturn("Booking cancelled successfully");
    }
}
