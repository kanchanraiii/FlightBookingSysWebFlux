package com.flightBooking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="airline")
@Data
public class Airline {
	
	@Id
	private String airlineId;
	
	@Indexed(unique=true)
	private String airlineCode;
	private String airlineName;
	

}
