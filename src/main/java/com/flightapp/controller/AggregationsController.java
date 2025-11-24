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

    // to get flights per airline
    @GetMapping("/flights-per-airline")
    public Flux<AirlineFlightCount> flightsPerAirline() {
        return service.flightsPerAirline();
    }

    // to get average price of each route
    @GetMapping("/avg-price-route")
    public Flux<RoutePrices> avgPricePerRoute() {
        return service.avgPricePerRoute();
    }

    // to get top destinations
    @GetMapping("/top-destinations")
    public Flux<PopularDestinations> topDestinations() {
        return service.topDestinations();
    }

    // to get seats available per air line
    @GetMapping("/seats-per-airline")
    public Flux<AirlineSeats> seatsPerAirline() {
        return service.seatsPerAirline();
    }

    // to get future flights
    @GetMapping("/upcoming-flights")
    public Flux<FlightsPerDay> upcomingFlights() {
        return service.upcomingFlightsPerDay();
    }

    // to get which flight has meal available
    @GetMapping("/meal-availability")
    public Flux<FlightsWithMeal> mealStats() {
        return service.mealStats();
    }

    // to get highest priced flights
    @GetMapping("/top-expensive-flights")
    public Flux<HighestPriceFlights> expensiveFlights() {
        return service.expensiveFlights();
    }
}
