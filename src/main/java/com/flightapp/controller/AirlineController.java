package com.flightapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.flightapp.model.Airline;
import com.flightapp.request.AddAirlineRequest;
import com.flightapp.service.AirlineInventoryService;

@RestController
@RequestMapping("/api/flight")
public class AirlineController {

    @Autowired
    private AirlineInventoryService airlineService;

    // to add an airline
    @PostMapping("/addAirline")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Airline> addAirline(@RequestBody AddAirlineRequest req) {
        return airlineService.addAirline(req);
    }

    // to get airlines in db
    @GetMapping("/getAllAirlines")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Airline> getAllAirlines() {
        return airlineService.getAllAirlines();
    }

    // to get airline with its code
    @GetMapping("/getAirline/{code}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Airline> getAirline(@PathVariable String code) {
        return airlineService.getAirline(code);
    }
}
