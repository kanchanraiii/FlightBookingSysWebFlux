package com.flightBooking.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="flightInventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightInventory {
	
	@Id
	private String flightId;
	
	private String flightNumber;
	private String airlineId; // fk -> airline
	private Cities sourceCity;
    private Cities destinationCity;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private boolean mealAvailable;
    private int totalSeats;
    private int availableSeats;
    private double price;

}
