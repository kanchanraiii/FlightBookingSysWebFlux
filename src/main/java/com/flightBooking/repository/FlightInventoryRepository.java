package com.flightBooking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.FlightInventory;

public interface FlightInventoryRepository extends ReactiveMongoRepository<FlightInventory,String>{

}
