package com.flightapp;

import org.junit.jupiter.api.Test;

import com.flightapp.aggregations.FlightsPerDay;

import static org.junit.jupiter.api.Assertions.*;

class FlightsPerDayTest {

    @Test
    void testDtoFields() {
        FlightsPerDay dto = new FlightsPerDay("2025-11-30", 12L);

        assertEquals("2025-11-30", dto.getDate());
        assertEquals(12L, dto.getTotalFlights());
    }
}
