package com.flightBooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import com.flightBooking.model.FlightInventory;
import com.flightBooking.request.AddFlightInventoryRequest;
import com.flightBooking.service.FlightInventoryService;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/flight")
public class FlightInventoryController {
	
	@Autowired
	private FlightInventoryService FlightInventoryService;
	
	// to add a flight in flightInventory
	@PostMapping("/airline/inventory/add")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<FlightInventory> addInventory(@Valid @RequestBody AddFlightInventoryRequest req){
		 return FlightInventoryService.addInventory(req);
	}

}
