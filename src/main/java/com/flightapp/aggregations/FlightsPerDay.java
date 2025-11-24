package com.flightapp.aggregations;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlightsPerDay {
    private String date;
    private long totalFlights;
}
