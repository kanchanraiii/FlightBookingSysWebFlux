package com.flightBooking.aggregations;

public class HighestPriceFlights {
    
	// to get five most expensive flights
	
	private String flightNumber;
    private double price;

    public String getFlightNumber() { 
    	return flightNumber; 
    }
    public void setFlightNumber(String flightNumber) { 
    	this.flightNumber = flightNumber; 
    }

    public double getPrice() { 
    	return price; 
    }
    public void setPrice(double price) { 
    	this.price = price; 
    }
}
