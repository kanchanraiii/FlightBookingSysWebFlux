package com.flightapp.aggregations;

public class PopularDestinations {
	
	// to get popular destinations
	
    private String destinationCity;
    private long flightCount;

    public String getDestinationCity() { 
    	return destinationCity; 
    }
    public void setDestinationCity(String destinationCity) { 
    	this.destinationCity = destinationCity; 
    }

    public long getFlightCount() { 
    	return flightCount; 
    }
    public void setFlightCount(long flightCount) { 
    	this.flightCount = flightCount; 
    }
}
