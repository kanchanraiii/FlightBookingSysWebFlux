package com.flightBooking.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightBooking.exceptions.ResourceNotFoundException;
import com.flightBooking.exceptions.ValidationException;
import com.flightBooking.model.FlightInventory;
import com.flightBooking.repository.AirlineRepository;
import com.flightBooking.repository.FlightInventoryRepository;
import com.flightBooking.request.AddFlightInventoryRequest;

import reactor.core.publisher.Mono;

@Service
public class FlightInventoryService {

    @Autowired
    private FlightInventoryRepository flightInventoryRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    public Mono<FlightInventory> addInventory(AddFlightInventoryRequest req) {

        if (req.getFlightNumber() == null || req.getFlightNumber().isBlank()) {
            return Mono.error(new ValidationException("Flight number is required"));
        }

        if (req.getSourceCity() == null) {
            return Mono.error(new ValidationException("Source city is required"));
        }

        if (req.getDestinationCity() == null) {
            return Mono.error(new ValidationException("Destination city is required"));
        }

        if (req.getSourceCity().equals(req.getDestinationCity())) {
            return Mono.error(new ValidationException("Source and destination cannot be the same"));
        }

        if (req.getDepartureDate() == null || req.getDepartureTime() == null) {
            return Mono.error(new ValidationException("Departure date & time are required"));
        }

        if (req.getArrivalDate() == null || req.getArrivalTime() == null) {
            return Mono.error(new ValidationException("Arrival date & time are required"));
        }

        if (req.getTotalSeats() == null || req.getTotalSeats() <= 0) {
            return Mono.error(new ValidationException("Total seats must be greater than 0"));
        }

        if (req.getPrice() == null || req.getPrice() <= 0) {
            return Mono.error(new ValidationException("Price must be greater than 0"));
        }

        LocalDateTime departureDT = LocalDateTime.of(req.getDepartureDate(), req.getDepartureTime());
        LocalDateTime arrivalDT = LocalDateTime.of(req.getArrivalDate(), req.getArrivalTime());

        if (!departureDT.isAfter(LocalDateTime.now())) {
            return Mono.error(new ValidationException("Departure must be in the future"));
        }

        if (!arrivalDT.isAfter(departureDT)) {
            return Mono.error(new ValidationException("Arrival must be after departure"));
        }

        return airlineRepository.findById(req.getAirlineCode())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Airline not found")))
                .flatMap(airline ->
                        flightInventoryRepository
                                .findFirstByFlightNumberAndDepartureDate(
                                        req.getFlightNumber(),
                                        req.getDepartureDate()
                                )
                                .flatMap(existing ->
                                        Mono.<FlightInventory>error(
                                                new ValidationException("Flight already exists on this date")
                                        )
                                )
                                .switchIfEmpty(Mono.defer(() -> {
                                    FlightInventory inv = new FlightInventory();
                                    inv.setAirlineCode(airline.getAirlineCode());
                                    inv.setFlightNumber(req.getFlightNumber());
                                    inv.setSourceCity(req.getSourceCity());
                                    inv.setDestinationCity(req.getDestinationCity());
                                    inv.setDepartureDate(req.getDepartureDate());
                                    inv.setDepartureTime(req.getDepartureTime());
                                    inv.setArrivalDate(req.getArrivalDate());
                                    inv.setArrivalTime(req.getArrivalTime());
                                    inv.setMealAvailable(req.isMealAvailable());
                                    inv.setTotalSeats(req.getTotalSeats());
                                    inv.setAvailableSeats(req.getTotalSeats());
                                    inv.setPrice(req.getPrice());

                                    return flightInventoryRepository.save(inv);
                                }))
                );
    }
}
