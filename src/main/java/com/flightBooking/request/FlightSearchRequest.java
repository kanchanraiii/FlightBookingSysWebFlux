package com.flightBooking.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.flightBooking.model.Cities;
import com.flightBooking.model.TripType;

public class FlightSearchRequest {

    @NotNull
    private Cities sourceCity;

    @NotNull
    private Cities destinationCity;

    @NotNull
    private LocalDate travelDate;

    @NotNull
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
