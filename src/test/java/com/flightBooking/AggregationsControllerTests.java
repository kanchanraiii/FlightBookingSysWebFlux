package com.flightBooking;

import com.flightBooking.controller.AggregationsController;
import com.flightBooking.service.AggregationsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@WebFluxTest(AggregationsController.class)
class AggregationsControllerTest {

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private AggregationsService service;

    @Test
    void testAvgPriceEndpoint() {
        org.mockito.Mockito.when(service.avgPricePerRoute()).thenReturn(Flux.empty());

        client.get().uri("/api/analytics/avg-route-price")
                .exchange()
                .expectStatus().isOk();
    }
}
