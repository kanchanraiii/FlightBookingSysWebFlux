package com.flightBooking.aggregations;

public class RoutePrices {
	
	// to get average price per route

	private String sourceCity;
    private String destinationCity;
    private Double averagePrice;

    public RoutePrices(String sourceCity, String destinationCity, Double averagePrice) {
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.averagePrice = averagePrice;
    }

    public String getSourceCity() { 
    	return sourceCity; 
    }
    public String getDestinationCity() { 
    	return destinationCity; 
    }
    public Double getAveragePrice() { 
    	return averagePrice; 
    }

	public void setSourceCity(String sourceCity) {
		this.sourceCity = sourceCity;
	}

	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}

	public void setAveragePrice(Double averagePrice) {
		this.averagePrice = averagePrice;
	}
}

