package com.flightapp;

import com.flightapp.model.Airline;
import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
import com.flightapp.model.Cities;
import com.flightapp.model.Gender;
import com.flightapp.model.Meal;
import com.flightapp.model.Passenger;
import com.flightapp.model.Seat;
import com.flightapp.model.TripType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelTests {

    @Test
    void testAirlineModel() {
        Airline airline = new Airline();
        airline.setAirlineCode("AI");
        airline.setAirlineName("Air India");

        assertEquals("AI", airline.getAirlineCode());
        assertEquals("Air India", airline.getAirlineName());
    }

    @Test
    void testSeatModel() {
        Seat seat = new Seat();
        seat.setSeatId("S1");
        seat.setSeatNo("12A");
        seat.setFlightId("FL1");
        seat.setAvailable(true);

        assertEquals("S1", seat.getSeatId());
        assertEquals("FL1", seat.getFlightId());
        assertEquals("12A", seat.getSeatNo());
        assertTrue(seat.isAvailable());
    }

    @Test
    void testPassengerModel() {
        Passenger p = new Passenger();
        p.setPassengerId("P10");
        p.setBookingId("B1");
        p.setName("John Doe");
        p.setAge(25);
        p.setGender(Gender.MALE);
        p.setSeatOutbound("14B");
        p.setSeatReturn("16C");
        p.setMeal(Meal.VEG);

        assertEquals("P10", p.getPassengerId());
        assertEquals("B1", p.getBookingId());
        assertEquals("John Doe", p.getName());
        assertEquals(25, p.getAge());
        assertEquals(Gender.MALE, p.getGender());
        assertEquals("14B", p.getSeatOutbound());
        assertEquals("16C", p.getSeatReturn());
        assertEquals(Meal.VEG, p.getMeal());
    }

    @Test
    void testBookingModel() {
        Booking booking = new Booking();
        booking.setBookingId("B99");
        booking.setOutboundFlightId("FL-OUT");
        booking.setReturnFlight("FL-RET");
        booking.setPnrOutbound("PNR123");
        booking.setPnrReturn("PNR789");
        booking.setContactName("Alice");
        booking.setContactEmail("alice@gmail.com");
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPassengers(3);
        booking.setTripType(TripType.ROUND_TRIP);

        assertEquals("B99", booking.getBookingId());
        assertEquals("FL-OUT", booking.getOutboundFlightId());
        assertEquals("FL-RET", booking.getReturnFlight());
        assertEquals("PNR123", booking.getPnrOutbound());
        assertEquals("PNR789", booking.getPnrReturn());
        assertEquals("Alice", booking.getContactName());
        assertEquals("alice@gmail.com", booking.getContactEmail());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(3, booking.getTotalPassengers());
        assertEquals(TripType.ROUND_TRIP, booking.getTripType());
    }

    @Test
    void testEnums() {
        assertEquals(BookingStatus.CONFIRMED, BookingStatus.valueOf("CONFIRMED"));
        assertEquals(TripType.ONE_WAY, TripType.valueOf("ONE_WAY"));
        assertEquals(Meal.NON_VEG, Meal.valueOf("NON_VEG"));
        assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
        assertEquals(11, Cities.values().length);
    }
}
