package com.flightBooking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightBooking.model.Booking;

public interface BookingRepository extends ReactiveMongoRepository<Booking,String> {

}
