package com.flightBooking.repository;

import java.time.LocalDate;

import com.flightBooking.aggregations.*;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.Cities;
import com.flightBooking.model.FlightInventory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightInventoryRepository extends ReactiveMongoRepository<FlightInventory, String> {

    Flux<FlightInventory> findBySourceCityAndDestinationCityAndDepartureDate(
            Cities sourceCity,
            Cities destinationCity,
            LocalDate travelDate
    );

    Mono<FlightInventory> findFirstByFlightNumberAndDepartureDate(String flightNumber, LocalDate departureDate);


    @Aggregation(pipeline = {
        "{ $group: { _id: '$airlineCode', totalFlights: { $sum: 1 } }}",
        "{ $project: { airlineCode: '$_id', totalFlights: 1, _id: 0 }}"
    })
    Flux<AirlineFlightCount> getFlightsPerAirline();


    @Aggregation(pipeline = {
        "{ $group: { _id: '$airlineCode', totalAvailableSeats: { $sum: '$availableSeats' } }}",
        "{ $project: { airlineCode: '$_id', totalAvailableSeats: 1, _id: 0 }}"
    })
    Flux<AirlineSeats> getSeatStatsPerAirline();


    @Aggregation(pipeline = {
        "{ $sort: { price: -1 }}",
        "{ $limit: 5 }",
        "{ $project: { flightNumber: 1, price: 1, _id: 0 }}"
    })
    Flux<HighestPriceFlights> getTopExpensiveFlights();


    @Aggregation(pipeline = {
        "{ $group: { _id: { src: '$sourceCity', dest: '$destinationCity' }, avgPrice: { $avg: '$price' } }}",
        "{ $project: { sourceCity: '$_id.src', destinationCity: '$_id.dest', averagePrice: '$avgPrice', _id: 0 }}"
    })
    Flux<RoutePrices> getAveragePricePerRoute();


    @Aggregation(pipeline = {
        "{ $group: { _id: '$destinationCity', count: { $sum: 1 } }}",
        "{ $sort: { count: -1 }}",
        "{ $limit: 5 }",
        "{ $project: { destinationCity: '$_id', flightCount: '$count', _id: 0 }}"
    })
    Flux<PopularDestinations> getTopDestinations();


    @Aggregation(pipeline = {
        "{ $match: { departureDate: { $gte: ISODate() } }}",
        "{ $group: { _id: '$departureDate', totalFlights: { $sum: 1 } }}",
        "{ $project: { departureDate: '$_id', totalFlights: 1, _id: 0 }}"
    })
    Flux<FlightsPerDay> getUpcomingFlightCounts();


    @Aggregation(pipeline = {
        "{ $group: { _id: '$mealAvailable', count: { $sum: 1 } }}",
        "{ $project: { mealAvailable: '$_id', count: 1, _id: 0 }}"
    })
    Flux<FlightsWithMeal> getMealAvailabilityStats();
}
