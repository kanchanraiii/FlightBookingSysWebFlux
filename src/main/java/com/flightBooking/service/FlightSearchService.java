package com.flightBooking.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import com.flightBooking.model.FlightInventory;
import com.flightBooking.repository.FlightInventoryRepository;
import com.flightBooking.request.FlightSearchRequest;

@Service
public class FlightSearchService {

    @Autowired
    private FlightInventoryRepository flightInventoryRepository;

    // to search a flight 
    public Flux<FlightInventory> searchFlights(FlightSearchRequest req) {
        return flightInventoryRepository
                .findBySourceCityAndDestinationCityAndDepartureDate(
                        req.getSourceCity(),
                        req.getDestinationCity(),
                        req.getTravelDate()
                );
    }
}
