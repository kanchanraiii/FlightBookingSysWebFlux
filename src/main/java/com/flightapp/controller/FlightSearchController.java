package com.flightapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import com.flightapp.model.FlightInventory;
import com.flightapp.request.FlightSearchRequest;
import com.flightapp.service.FlightSearchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/flight")
public class FlightSearchController {

    @Autowired
    private FlightSearchService searchService;

    // to search a flight
    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Flux<FlightInventory> searchFlights(@Valid @RequestBody FlightSearchRequest req) {
        return searchService.searchFlights(req);
    }
}
