package com.flightBooking;

import com.flightBooking.model.Cities;
import com.flightBooking.model.FlightInventory;
import com.flightBooking.repository.FlightInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

@DataMongoTest
class AggregationTests {

    @Autowired
    private FlightInventoryRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll().block();

        FlightInventory f1 = new FlightInventory();
        f1.setAirlineCode("INDG");
        f1.setFlightNumber("6E11");
        f1.setSourceCity(Cities.CHENNAI);
        f1.setDestinationCity(Cities.DELHI);
        f1.setDepartureDate(LocalDate.now().plusDays(1));
        f1.setDepartureTime(LocalTime.NOON);
        f1.setArrivalDate(LocalDate.now().plusDays(1));
        f1.setArrivalTime(LocalTime.of(14, 10));
        f1.setMealAvailable(true);
        f1.setTotalSeats(180);
        f1.setAvailableSeats(120);
        f1.setPrice(6000.0);

        FlightInventory f2 = new FlightInventory();
        f2.setAirlineCode("VIST");
        f2.setFlightNumber("UK22");
        f2.setSourceCity(Cities.MUMBAI);
        f2.setDestinationCity(Cities.CHENNAI);
        f2.setDepartureDate(LocalDate.now().plusDays(3));
        f2.setDepartureTime(LocalTime.of(7, 50));
        f2.setArrivalDate(LocalDate.now().plusDays(3));
        f2.setArrivalTime(LocalTime.of(9, 40));
        f2.setMealAvailable(false);
        f2.setTotalSeats(160);
        f2.setAvailableSeats(90);
        f2.setPrice(7200.0);

        repo.saveAll(Flux.just(f1, f2)).blockLast();
    }

    @Test
    void testAveragePricePerRoute() {
        StepVerifier.create(repo.getAveragePricePerRoute())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testTopDestinations() {
        StepVerifier.create(repo.getTopDestinations())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testUpcomingFlightCounts() {
        StepVerifier.create(repo.getUpcomingFlightCounts())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testMealAvailabilityStats() {
        StepVerifier.create(repo.getMealAvailabilityStats())
                .expectNextCount(2)
                .verifyComplete();
    }
}
