package com.flightBooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.flightBooking.model.Airline;
import com.flightBooking.repository.AirlineRepository;
import com.flightBooking.request.AddAirlineRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AirlineInventoryService {
	
	@Autowired
	private AirlineRepository AirlineRepository;
	
	// adding airline
	public Mono<Airline> addAirline(AddAirlineRequest req){
		Airline airline=new Airline();
		airline.setAirlineCode(req.getAirlineCode());
	    airline.setAirlineName(req.getAirlineName());
	    return AirlineRepository.save(airline);
	}
	
	// fetch all airlines
	public Flux<Airline> getAllAirlines() {
        return AirlineRepository.findAll();
    }
	
	// fetch an airline by airline code
	public Mono<Airline> getAirline(String code) {
	        return AirlineRepository.findById(code);
	}

}
