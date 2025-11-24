package com.flightapp.controller;

import org.springframework.web.bind.annotation.*;
import com.flightapp.service.AggregationsService;
import com.flightapp.aggregations.*;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/flight/aggregations")
public class AggregationsController {

    @Autowired
    private AggregationsService service;

    @GetMapping("/flights-per-airline")
    public Flux<AirlineFlightCount> flightsPerAirline() {
        return service.flightsPerAirline();
    }

    @GetMapping("/avg-price-route")
    public Flux<RoutePrices> avgPricePerRoute() {
        return service.avgPricePerRoute();
    }

    @GetMapping("/top-destinations")
    public Flux<PopularDestinations> topDestinations() {
        return service.topDestinations();
    }

    @GetMapping("/seats-per-airline")
    public Flux<AirlineSeats> seatsPerAirline() {
        return service.seatsPerAirline();
    }

    @GetMapping("/upcoming-flights")
    public Flux<FlightsPerDay> upcomingFlights() {
        return service.upcomingFlightsPerDay();
    }

    @GetMapping("/meal-availability")
    public Flux<FlightsWithMeal> mealStats() {
        return service.mealStats();
    }

    @GetMapping("/top-expensive-flights")
    public Flux<HighestPriceFlights> expensiveFlights() {
        return service.expensiveFlights();
    }
}
