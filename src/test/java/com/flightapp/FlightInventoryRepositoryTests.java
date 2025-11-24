package com.flightapp;
import com.flightapp.model.Cities;
import com.flightapp.model.FlightInventory;
import com.flightapp.repository.FlightInventoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

@DataMongoTest
class FlightInventoryRepositoryTest {

    @Autowired
    private FlightInventoryRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll().block();

        FlightInventory f1 = new FlightInventory();
        f1.setAirlineCode("INDG");
        f1.setFlightNumber("6E502");
        f1.setSourceCity(Cities.CHENNAI);
        f1.setDestinationCity(Cities.MUMBAI);
        f1.setDepartureDate(LocalDate.now().plusDays(2));
        f1.setDepartureTime(LocalTime.of(9, 30));
        f1.setArrivalDate(LocalDate.now().plusDays(2));
        f1.setArrivalTime(LocalTime.of(11, 45));
        f1.setMealAvailable(true);
        f1.setTotalSeats(180);
        f1.setAvailableSeats(180);
        f1.setPrice(6000.0);

        FlightInventory f2 = new FlightInventory();
        f2.setAirlineCode("VIST");
        f2.setFlightNumber("UK801");
        f2.setSourceCity(Cities.CHENNAI);
        f2.setDestinationCity(Cities.DELHI);
        f2.setDepartureDate(LocalDate.now().plusDays(2));
        f2.setDepartureTime(LocalTime.of(14, 10));
        f2.setArrivalDate(LocalDate.now().plusDays(2));
        f2.setArrivalTime(LocalTime.of(16, 45));
        f2.setMealAvailable(false);
        f2.setTotalSeats(150);
        f2.setAvailableSeats(150);
        f2.setPrice(4500.0);

        FlightInventory f3 = new FlightInventory();
        f3.setAirlineCode("INDG");
        f3.setFlightNumber("6E900");
        f3.setSourceCity(Cities.MUMBAI);
        f3.setDestinationCity(Cities.BANGALORE);
        f3.setDepartureDate(LocalDate.now().plusDays(5));
        f3.setDepartureTime(LocalTime.of(7, 50));
        f3.setArrivalDate(LocalDate.now().plusDays(5));
        f3.setArrivalTime(LocalTime.of(9, 40));
        f3.setMealAvailable(true);
        f3.setTotalSeats(200);
        f3.setAvailableSeats(200);
        f3.setPrice(7200.0);

        repo.saveAll(Flux.just(f1, f2, f3)).blockLast();
    }

    @Test
    void testAveragePricePerRoute() {
        StepVerifier.create(repo.getAveragePricePerRoute())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testTopDestinations() {
        StepVerifier.create(repo.getTopDestinations())
                .expectNextCount(3) 
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
