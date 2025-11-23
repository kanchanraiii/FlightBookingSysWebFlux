package com.flightBooking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection="seats")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Seat {

    @Id
    private String seatId;
    
    private String flightId; // fk -> flightInventory
    private String seatNo;
    private boolean available;
}
