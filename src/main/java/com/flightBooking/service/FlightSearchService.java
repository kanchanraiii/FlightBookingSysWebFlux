package com.flightBooking.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightBooking.exceptions.ResourceNotFoundException;
import com.flightBooking.exceptions.ValidationException;
import com.flightBooking.model.FlightInventory;
import com.flightBooking.repository.FlightInventoryRepository;
import com.flightBooking.request.FlightSearchRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlightSearchService {

    @Autowired
    private FlightInventoryRepository flightInventoryRepository;

    // to search a flight 
    public Flux<FlightInventory> searchFlights(FlightSearchRequest req) {

        if (req.getSourceCity() == null) {
            return Flux.error(new ValidationException("Source city is required"));
        }

        if (req.getDestinationCity() == null) {
            return Flux.error(new ValidationException("Destination city is required"));
        }

        if (req.getSourceCity().equals(req.getDestinationCity())) {
            return Flux.error(new ValidationException("Source and destination cannot be the same"));
        }

        if (req.getTravelDate() == null) {
            return Flux.error(new ValidationException("Travel date is required"));
        }

        if (req.getTravelDate().isBefore(LocalDate.now())) {
            return Flux.error(new ValidationException("Travel date cannot be in the past"));
        }

        return flightInventoryRepository
                .findBySourceCityAndDestinationCityAndDepartureDate(
                        req.getSourceCity(),
                        req.getDestinationCity(),
                        req.getTravelDate()
                )
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No flights found")));
    }
}
