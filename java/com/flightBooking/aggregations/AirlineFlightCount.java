package com.flightBooking.aggregations;

public class AirlineFlightCount {
	
	// to count flights per airline
	
	private String airlineCode;
	private Long totalFlights;
	
	public AirlineFlightCount(String airlineCode, Long totalFlights) {
        this.airlineCode = airlineCode;
        this.totalFlights = totalFlights;
    }

	public String getAirlineCode() {
		return airlineCode;
	}

	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public Long getTotalFlights() {
		return totalFlights;
	}

	public void setTotalFlights(Long totalFlights) {
		this.totalFlights = totalFlights;
	}
	
	
	

}
