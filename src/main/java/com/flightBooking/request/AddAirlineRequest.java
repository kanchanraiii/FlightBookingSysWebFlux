package com.flightBooking.request;

import jakarta.validation.constraints.NotBlank;

public class AddAirlineRequest {

    @NotBlank
    private String airlineCode; 

    @NotBlank
    private String airlineName;

    // getters and setters
    public String getAirlineCode() {
        return airlineCode;
    }
    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getAirlineName() {
        return airlineName;
    }
    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }
}
