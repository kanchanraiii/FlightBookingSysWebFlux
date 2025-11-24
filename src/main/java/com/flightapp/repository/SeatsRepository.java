package com.flightapp.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flightapp.model.Seat;

public interface SeatsRepository extends ReactiveMongoRepository<Seat,String>{

}
