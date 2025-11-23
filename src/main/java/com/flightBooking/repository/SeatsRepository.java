package com.flightBooking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.Seat;

public interface SeatsRepository extends ReactiveMongoRepository<Seat,String>{

}
