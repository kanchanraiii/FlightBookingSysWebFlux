package com.flightBooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightBooking.model.FlightInventory;
import com.flightBooking.repository.*;
import com.flightBooking.request.AddFlightInventoryRequest;

import reactor.core.publisher.Mono;


@Service
public class FlightInventoryService {
	
	@Autowired
	private FlightInventoryRepository FlightInventoryRepository;
	
	@Autowired
	private AirlineRepository AirlineRepository;
	
	// add a flight to the flight inventory
	public Mono<FlightInventory> addInventory(AddFlightInventoryRequest req) {
        return AirlineRepository.findById(req.getAirlineCode())
                .flatMap(airline -> {
                    FlightInventory inv = new FlightInventory();
                    inv.setAirlineCode(airline.getAirlineCode());
                    inv.setFlightNumber(req.getFlightNumber());
                    inv.setSourceCity(req.getSourceCity());
                    inv.setDestinationCity(req.getDestinationCity());
                    inv.setDepartureDate(req.getDepartureDate());
                    inv.setDepartureTime(req.getDepartureTime());
                    inv.setArrivalDate(req.getArrivalDate());
                    inv.setArrivalTime(req.getArrivalTime());
                    inv.setMealAvailable(req.isMealAvailable());
                    inv.setTotalSeats(req.getTotalSeats());
                    inv.setAvailableSeats(req.getTotalSeats());
                    inv.setPrice(req.getPrice());
                    return FlightInventoryRepository.save(inv);
                });
    }

}
