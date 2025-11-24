package com.flightBooking.aggregations;

import java.time.LocalDate;

public class FlightsPerDay {
    
	// to get flights per day
	
	private LocalDate departureDate;
    private long totalFlights;

    public LocalDate getDepartureDate() { 
    	return departureDate; 
    }
    public void setDepartureDate(LocalDate departureDate) { 
    	this.departureDate = departureDate; 
    }

    public long getTotalFlights() { 
    	return totalFlights; 
    }
    public void setTotalFlights(long totalFlights) { 
    	this.totalFlights = totalFlights; 
    }
}
