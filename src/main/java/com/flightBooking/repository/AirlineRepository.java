package com.flightBooking.repository;
import com.flightBooking.aggregations.AirlineFlightCount;
import com.flightBooking.aggregations.AirlineSeats;
import com.flightBooking.aggregations.HighestPriceFlights;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.Airline;

import reactor.core.publisher.Flux;

public interface AirlineRepository extends ReactiveMongoRepository<Airline,String> {
	
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
		    "{ $project: { flightNumber: 1, price: 1 }}"
		})
		Flux<HighestPriceFlights> getTopExpensiveFlights();


}
