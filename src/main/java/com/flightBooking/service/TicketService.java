package com.flightBooking.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import com.flightBooking.model.Booking;
import com.flightBooking.repository.BookingRepository;

@Service
public class TicketService {

    @Autowired
    private BookingRepository bookingRepository;

    // to get ticket by pnr
    public Mono<Booking> getTicketByPnr(String pnr) {
        return bookingRepository.findByPnrOutbound(pnr)
                .switchIfEmpty(bookingRepository.findByPnrReturn(pnr));
    }
}
