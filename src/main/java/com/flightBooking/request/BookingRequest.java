package com.flightBooking.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.flightBooking.model.TripType;

public class BookingRequest {

    @NotBlank(message="Outbound flight is a required field")
    private String outboundFlightId; 

    private String returnFlightId; 

    @NotBlank(message="Contact name is a required field")
    private String contactName;

    @Email(message="Invalid email format")
    @NotBlank(message="Email cannot be empty")
    private String contactEmail;

    @NotNull(message="Trip Type is a required field either ONE_WAY or ROUND_TRIP")
    private TripType tripType;

    @Valid
    @NotNull(message="Passenger list is required")
    @Size(min=1, message="At least one passenger is required")
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
