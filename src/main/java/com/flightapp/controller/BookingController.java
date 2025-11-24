package com.flightapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.model.Booking;
import com.flightapp.request.BookingRequest;
import com.flightapp.service.BookingService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/flight")
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	
	// to  book a flight with flightId
	@PostMapping("/booking/{flightId}")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Booking> bookFlight(@PathVariable String flightId, @Valid @RequestBody BookingRequest req){
		return bookingService.bookFlight(flightId, req);		
	}
}


