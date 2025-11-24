package com.flightBooking.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.Airline;

public interface AirlineRepository extends ReactiveMongoRepository<Airline,String> {
	
}
