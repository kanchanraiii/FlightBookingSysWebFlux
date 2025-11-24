package com.flightBooking.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.flightBooking.service.AggregationsService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/flight/aggregations")
public class AggregationsController {

    @Autowired
    private AggregationsService service;

    @GetMapping("/flights-per-airline")
    public Flux<?> flightsPerAirline() { 
    	return service.flightsPerAirline(); 
    }

    @GetMapping("/avg-price-route")
    public Flux<?> avgPricePerRoute() { 
    	return service.avgPricePerRoute(); 
    }

    @GetMapping("/top-destinations")
    public Flux<?> topDestinations() { 
    	return service.topDestinations(); 
    }

    @GetMapping("/seats-per-airline")
    public Flux<?> seatsPerAirline() { 
    	return service.seatsPerAirline(); 
    }

    @GetMapping("/upcoming-flights")
    public Flux<?> upcomingFlights() { 
    	return service.upcomingFlightsPerDay(); 
    }

    @GetMapping("/meal-availability")
    public Flux<?> mealStats() { 
    	return service.mealStats(); 
    }

    @GetMapping("/top-expensive-flights")
    public Flux<?> expensiveFlights() { 
    	return service.expensiveFlights(); 
    }
}
