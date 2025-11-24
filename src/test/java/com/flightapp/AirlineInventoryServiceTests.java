package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirlineInventoryServiceTests {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineInventoryService airlineService;

    private AddAirlineRequest request(String code, String name) {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setAirlineCode(code);
        req.setAirlineName(name);
        return req;
    }

    @Test
    @DisplayName("Add airline uppercases code and saves when not existing")
    void addAirlineHappyPath() {
        AddAirlineRequest req = request("ai", "Air India");
        when(airlineRepository.existsById("AI")).thenReturn(Mono.just(false));
        when(airlineRepository.save(any(Airline.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0, Airline.class)));

        StepVerifier.create(airlineService.addAirline(req))
                .assertNext(saved -> {
                    assertEquals("AI", saved.getAirlineCode());
                    assertEquals("Air India", saved.getAirlineName());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Reject duplicate airline codes")
    void rejectDuplicateAirline() {
        when(airlineRepository.existsById("AI")).thenReturn(Mono.just(true));
        StepVerifier.create(airlineService.addAirline(request("AI", "Name")))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Reject missing airline name")
    void rejectMissingName() {
        StepVerifier.create(airlineService.addAirline(request("AI", " ")))
                .verifyError(ValidationException.class);
    }

    @Test
    @DisplayName("Get all airlines delegates to repository")
    void getAllAirlines() {
        when(airlineRepository.findAll()).thenReturn(Flux.just(new Airline(), new Airline()));
        StepVerifier.create(airlineService.getAllAirlines())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Get airline by code uppercases input and throws when missing")
    void getAirlineNotFound() {
        when(airlineRepository.findById(anyString())).thenReturn(Mono.empty());
        StepVerifier.create(airlineService.getAirline("ai"))
                .verifyError(ResourceNotFoundException.class);
    }
}
