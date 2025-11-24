package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.model.Airline;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.request.AddAirlineRequest;
import com.flightapp.service.AirlineInventoryService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirlineInventoryServiceTests {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineInventoryService airlineInventoryService;

    private AddAirlineRequest req() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setAirlineCode("AI");
        req.setAirlineName("Air India");
        return req;
    }

    @Test
    @DisplayName("Add airline persists code and name")
    void addAirline() {
        when(airlineRepository.existsById("AI")).thenReturn(Mono.just(false));
        when(airlineRepository.save(any(Airline.class)))
                .thenAnswer(inv -> {
                    Airline a = inv.getArgument(0);
                    return Mono.just(a);
                });

        StepVerifier.create(airlineInventoryService.addAirline(req()))
                .assertNext(a -> assertEquals("AI", a.getAirlineCode()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get all airlines returns flux from repository")
    void getAllAirlines() {
        when(airlineRepository.findAll()).thenReturn(Flux.just(new Airline(), new Airline()));

        StepVerifier.create(airlineInventoryService.getAllAirlines())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Get airline by code returns mono")
    void getAirline() {
        Airline airline = new Airline();
        airline.setAirlineCode("AI");
        when(airlineRepository.findById("AI")).thenReturn(Mono.just(airline));

        StepVerifier.create(airlineInventoryService.getAirline("AI"))
                .assertNext(a -> assertEquals("AI", a.getAirlineCode()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get airline returns empty when not found")
    void getAirlineNotFound() {
        when(airlineRepository.findById("XX")).thenReturn(Mono.empty());

        StepVerifier.create(airlineInventoryService.getAirline("XX"))
                .verifyError(ResourceNotFoundException.class);
    }
}
