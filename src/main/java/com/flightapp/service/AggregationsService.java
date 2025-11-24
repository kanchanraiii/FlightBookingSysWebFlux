package com.flightapp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import com.flightapp.aggregations.*;
import com.flightapp.repository.FlightInventoryRepository;

@Service
public class AggregationsService {

    @Autowired
    private FlightInventoryRepository flightRepo;

    public Flux<AirlineFlightCount> flightsPerAirline() {
        return flightRepo.getFlightsPerAirline();
    }

    public Flux<RoutePrices> avgPricePerRoute() {
        return flightRepo.getAveragePricePerRoute();
    }

    public Flux<PopularDestinations> topDestinations() {
        return flightRepo.getTopDestinations();
    }

    public Flux<AirlineSeats> seatsPerAirline() {
        return flightRepo.getSeatStatsPerAirline();
    }

    public Flux<FlightsPerDay> upcomingFlightsPerDay() {
        return flightRepo.getUpcomingFlightCounts();
    }

    public Flux<FlightsWithMeal> mealStats() {
        return flightRepo.getMealAvailabilityStats();
    }

    public Flux<HighestPriceFlights> expensiveFlights() {
        return flightRepo.getTopExpensiveFlights();
    }
}
