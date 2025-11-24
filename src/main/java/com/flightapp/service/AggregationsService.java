package com.flightapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightInventoryRepository;

import reactor.core.publisher.Flux;

@Service
public class AggregationsService {
	
	@Autowired
    private FlightInventoryRepository flightRepo;
	
	public Flux<?> flightsPerAirline() { 
		return flightRepo.getFlightsPerAirline(); 
	}
    public Flux<?> avgPricePerRoute() { 
    	return flightRepo.getAveragePricePerRoute(); 
    }
    public Flux<?> topDestinations() { 
    	return flightRepo.getTopDestinations(); 
    }
    public Flux<?> seatsPerAirline() { 
    	return flightRepo.getSeatStatsPerAirline(); 
    }
    public Flux<?> upcomingFlightsPerDay() { 
    	return flightRepo.getUpcomingFlightCounts(); 
    }
    public Flux<?> mealStats() { 
    	return flightRepo.getMealAvailabilityStats(); 
    }
    public Flux<?> expensiveFlights() { 
    	return flightRepo.getTopExpensiveFlights(); 
    }

}
