package com.flightBooking.request;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.flightBooking.model.Cities;

public class AddFlightInventoryRequest {
	
	@NotBlank
	private String airlineCode;
	
	@NotBlank
	private String flightNumber;
	
	@NotNull
	private Cities sourceCity;
	
	@NotNull
	private Cities destinationCity;
	
	@NotNull
	private LocalDate departureDate;
	
	@NotNull
	private LocalTime departureTime;
	
	@NotNull
	private LocalDate arrivalDate;
	
	@NotNull
	private LocalTime arrivalTime;
	
	@NotNull
	@Positive
	private Integer totalSeats;
	
	@NotNull
	@Positive
	private Float price;
	
	private boolean mealAvailable;

	// getter and setters
	public String getAirlineCode() {
		return airlineCode;
	}

	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public Cities getSourceCity() {
		return sourceCity;
	}

	public void setSourceCity(Cities sourceCity) {
		this.sourceCity = sourceCity;
	}

	public Cities getDestinationCity() {
		return destinationCity;
	}

	public void setDestinationCity(Cities destinationCity) {
		this.destinationCity = destinationCity;
	}

	public LocalDate getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}

	public LocalTime getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}

	public LocalDate getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(LocalDate arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Integer getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public boolean isMealAvailable() {
		return mealAvailable;
	}

	public void setMealAvailable(boolean mealAvailable) {
		this.mealAvailable = mealAvailable;
	}
	

}
