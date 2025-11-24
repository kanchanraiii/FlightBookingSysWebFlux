package com.flightapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootTest(classes = FlightBookingSysWebFluxApplication.class)
class FlightBookingSysWebFluxApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainInvokesSpringApplication() {
		try (MockedStatic<SpringApplication> spring = Mockito.mockStatic(SpringApplication.class)) {
			ConfigurableApplicationContext ctx = Mockito.mock(ConfigurableApplicationContext.class);
			spring.when(() -> SpringApplication.run(FlightBookingSysWebFluxApplication.class, new String[]{}))
					.thenReturn(ctx);
			FlightBookingSysWebFluxApplication.main(new String[]{});
			spring.verify(() -> SpringApplication.run(FlightBookingSysWebFluxApplication.class, new String[]{}));
		}
	}

}
