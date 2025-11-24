package com.flightBooking.repository;

import java.time.LocalDate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.Cities;
import com.flightBooking.model.FlightInventory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface FlightInventoryRepository extends ReactiveMongoRepository<FlightInventory,String>{

	Flux<FlightInventory> findBySourceCityAndDestinationCityAndDepartureDate(
			Cities sourceCity, 
			Cities destinationCity, 
			LocalDate travelDate
	);

	Mono<FlightInventory> findFirstByFlightNumberAndDepartureDate(String flightNumber, LocalDate departureDate);

}
