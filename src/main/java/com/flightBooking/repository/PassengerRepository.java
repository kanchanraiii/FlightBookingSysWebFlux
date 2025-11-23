package com.flightBooking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.Passenger;

public interface PassengerRepository extends ReactiveMongoRepository<Passenger, String> {

}
