package com.flightBooking.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.flightBooking.model.Cities;
import com.flightBooking.model.TripType;

public class FlightSearchRequest {

    @NotNull(message="Source City cannot be empty")
    private Cities sourceCity;

    @NotNull(message="Destination city cannot be empty")
    private Cities destinationCity;

    @NotNull(message="Travel date cannot be empty")
    private LocalDate travelDate;

    @NotNull(message="Trip type is a required filed, either ONE_WAY or ROUND_TRIP")
    private TripType tripType; 

    private LocalDate returnDate;  // only required for round-trip


    // getters and setters
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

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
