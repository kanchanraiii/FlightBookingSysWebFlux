## Flight Booking Using WebFlux – Week 5 Assignment

This repository contains my solution for a Flight Booking System backend assignment, built with Spring Boot 3 and Java 17. The goal is to model a small airline booking platform, design the data model, expose REST APIs, and cover key business rules with tests.

---

### Screenshots & Output 

All screenshots are included in the PDF below:

➡️ [Screenshots – All Word Doc.pdf](./Screenshots%20-%20All%20Word%20Doc.pdf)

---

### The code achieves:

- Unit test coverage of **90%**
- Load Testing conducted using **JMeter** for **20, 50, 100 threads** across all endpoints
- Full flight inventory module add, book, cancel tickets & flights with 9 endpoints
- MongoDB Aggregation Pipelines for **7 use cases**  
  *(located under `FlightInventoryRepository.java`,`AggregationsController.java`,`com.flightapp.aggregations`)*
- Strong validation with proper status codes - including **201 for POST**
- Centralized exception handling via  
  `GlobalErrorHandler.java`, `ValidationException.java`, `ResourceNotFoundException.java` in `com.flightapp.exceptions`
- SonarQube analysis applied and reduced 87 vulnerabilities

---
### ➡️ ER Diragram
 ![ER Diagram](./ER%20Diagram%20-%20FlightBookingSys.drawio.png)

---

 Built with **Spring WebFlux, Reactive MongoDB, JUnit 5, Mockito, Jacoco, JMeter & SonarQube**
