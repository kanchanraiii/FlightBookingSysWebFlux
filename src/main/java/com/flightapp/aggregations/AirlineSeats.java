package com.flightapp.aggregations;

public class AirlineSeats {
	
	// to get total seats available in airlines
	
	private String airlineCode;
    private long totalAvailableSeats;

    public String getAirlineCode() { 
    	return airlineCode; 
    }
    public void setAirlineCode(String airlineCode) { 
    	this.airlineCode = airlineCode; 
    }

    public long getTotalAvailableSeats() { 
    	return totalAvailableSeats; 
    }
    public void setTotalAvailableSeats(long totalAvailableSeats) { 
    	this.totalAvailableSeats = totalAvailableSeats; 
    }

}
