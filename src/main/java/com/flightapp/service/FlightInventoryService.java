package com.flightapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.Seat;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.SeatsRepository;
import com.flightapp.request.AddFlightInventoryRequest;

import reactor.core.publisher.Mono;

@Service
public class FlightInventoryService {

    @Autowired
    private FlightInventoryRepository flightInventoryRepository;

    @Autowired
    private AirlineRepository airlineRepository;
    
    @Autowired
    private SeatsRepository seatRepository;


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

                                    return flightInventoryRepository.save(inv)
                                            .flatMap(saved ->
                                                    generateSeats(saved.getFlightId(), saved.getTotalSeats())
                                                            .thenReturn(saved)
                                            );

                                }))
                );
    }
    
    private Mono<Void> generateSeats(String flightId, int totalSeats) {
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= totalSeats; i++) {
            Seat s = new Seat();
            s.setFlightId(flightId);
            s.setSeatNo("S" + i);
            s.setBooked(false);
            seats.add(s);
        }

        return seatRepository.saveAll(seats).then();
    }

}
