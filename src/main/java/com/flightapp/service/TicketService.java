package com.flightapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Booking;
import com.flightapp.repository.BookingRepository;

import reactor.core.publisher.Mono;

@Service
public class TicketService {

    @Autowired
    private BookingRepository bookingRepository;

    // to get ticket by pnr
    public Mono<Booking> getTicketByPnr(String pnr) {

        if (pnr == null || pnr.trim().isEmpty()) {
            return Mono.error(new ValidationException("PNR cannot be empty"));
        }

        if (pnr.length() != 6) {
            return Mono.error(new ValidationException("PNR must be exactly 6 characters"));
        }

        if (!pnr.matches("^[A-Z0-9]+$")) {
            return Mono.error(new ValidationException("PNR must be alphanumeric"));
        }

        return bookingRepository.findByPnrOutbound(pnr)
                .switchIfEmpty(
                        bookingRepository.findByPnrReturn(pnr)
                                .switchIfEmpty(Mono.error(
                                        new ResourceNotFoundException("PNR not found")
                                ))
                );
    }
}
