package com.flightapp;

import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.service.AggregationsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregationsServiceTests {

    @Mock
    private FlightInventoryRepository flightRepo;

    @InjectMocks
    private AggregationsService aggregationsService;

    @Test
    @DisplayName("Price and destination aggregations are exposed")
    void otherAggregations() {
        when(flightRepo.getAveragePricePerRoute())
                .thenReturn(Flux.just(new com.flightapp.aggregations.RoutePrices("SRC", "DEST", 5000.0)));
        when(flightRepo.getTopDestinations())
                .thenReturn(Flux.just(new com.flightapp.aggregations.PopularDestinations()));
        when(flightRepo.getSeatStatsPerAirline())
                .thenReturn(Flux.just(new com.flightapp.aggregations.AirlineSeats()));
        when(flightRepo.getUpcomingFlightCounts())
                .thenReturn(Flux.just(new com.flightapp.aggregations.FlightsPerDay("2025-11-30", 3L)));
        when(flightRepo.getMealAvailabilityStats())
                .thenReturn(Flux.just(new com.flightapp.aggregations.FlightsWithMeal()));
        when(flightRepo.getTopExpensiveFlights())
                .thenReturn(Flux.just(new com.flightapp.aggregations.HighestPriceFlights()));

        StepVerifier.create(aggregationsService.avgPricePerRoute())
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(aggregationsService.topDestinations())
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(aggregationsService.seatsPerAirline())
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(aggregationsService.upcomingFlightsPerDay())
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(aggregationsService.mealStats())
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(aggregationsService.expensiveFlights())
                .expectNextCount(1)
                .verifyComplete();
    }
}
