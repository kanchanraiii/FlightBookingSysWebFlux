package com.flightapp;

import com.flightapp.model.Airline;
import com.flightapp.model.FlightInventory;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.model.Cities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

@DataMongoTest
class AirlineRepositoryTest {

	@Autowired
	private AirlineRepository airlineRepo;

	@Autowired
	private FlightInventoryRepository flightRepo;

	@BeforeEach
	void setup() {
		airlineRepo.deleteAll().block();
		flightRepo.deleteAll().block();

		Airline a1 = new Airline();
		a1.setAirlineCode("INDG");
		a1.setAirlineName("Indigo");

		Airline a2 = new Airline();
		a2.setAirlineCode("VIST");
		a2.setAirlineName("Vistara");

		airlineRepo.saveAll(Flux.just(a1, a2)).blockLast();

		FlightInventory f1 = new FlightInventory();
		f1.setAirlineCode("INDG");
		f1.setFlightNumber("6E502");
		f1.setSourceCity(Cities.CHENNAI);
		f1.setDestinationCity(Cities.MUMBAI);
		f1.setDepartureDate(LocalDate.now().plusDays(2));
		f1.setDepartureTime(LocalTime.of(12, 0));
		f1.setArrivalDate(LocalDate.now().plusDays(2));
		f1.setArrivalTime(LocalTime.of(14, 0));
		f1.setMealAvailable(true);
		f1.setTotalSeats(180);
		f1.setAvailableSeats(150);
		f1.setPrice(5000.0);

		FlightInventory f2 = new FlightInventory();
		f2.setAirlineCode("INDG");
		f2.setFlightNumber("6E808");
		f2.setSourceCity(Cities.MUMBAI);
		f2.setDestinationCity(Cities.DELHI);
		f2.setDepartureDate(LocalDate.now().plusDays(3));
		f2.setDepartureTime(LocalTime.of(7, 30));
		f2.setArrivalDate(LocalDate.now().plusDays(3));
		f2.setArrivalTime(LocalTime.of(9, 30));
		f2.setMealAvailable(false);
		f2.setTotalSeats(160);
		f2.setAvailableSeats(120);
		f2.setPrice(6200.0);

		FlightInventory f3 = new FlightInventory();
		f3.setAirlineCode("VIST");
		f3.setFlightNumber("UK701");
		f3.setSourceCity(Cities.BANGALORE);
		f3.setDestinationCity(Cities.KOLKATA);
		f3.setDepartureDate(LocalDate.now().plusDays(1));
		f3.setDepartureTime(LocalTime.of(8, 10));
		f3.setArrivalDate(LocalDate.now().plusDays(1));
		f3.setArrivalTime(LocalTime.of(12, 15));
		f3.setMealAvailable(true);
		f3.setTotalSeats(200);
		f3.setAvailableSeats(180);
		f3.setPrice(9000.0);

		flightRepo.saveAll(Flux.just(f1, f2, f3)).blockLast();
	}

	@Test
	void testFlightsPerAirline() {
		StepVerifier.create(flightRepo.getFlightsPerAirline()).expectNextCount(2).verifyComplete();
	}

	@Test
	void testSeatStatsPerAirline() {
		StepVerifier.create(flightRepo.getSeatStatsPerAirline()).expectNextCount(2).verifyComplete();
	}

}
