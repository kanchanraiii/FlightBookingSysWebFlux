package com.flightBooking.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.flightBooking.model.TripType;

public class BookingRequest {

    @NotBlank
    private String outboundFlightId; 

    private String returnFlightId; 

    @NotBlank
    private String contactName;

    @Email
    @NotBlank
    private String contactEmail;

    @NotNull
    private TripType tripType;

    @Valid
    @NotNull
    @Size
    private List<PassengerRequest> passengers;

    // getters and setters
    public String getOutboundFlightId() {
        return outboundFlightId;
    }
    public void setOutboundFlightId(String outboundFlightId) {
        this.outboundFlightId = outboundFlightId;
    }

    public String getReturnFlightId() {
        return returnFlightId;
    }
    public void setReturnFlightId(String returnFlightId) {
        this.returnFlightId = returnFlightId;
    }

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public TripType getTripType() {
        return tripType;
    }
    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    public List<PassengerRequest> getPassengers() {
        return passengers;
    }
    public void setPassengers(List<PassengerRequest> passengers) {
        this.passengers = passengers;
    }
}
