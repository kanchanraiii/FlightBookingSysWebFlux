## Flight Booking Using WebFlux – Week 5 Assignment

This repository contains my solution for a Flight Booking System backend assignment, built with **Spring Boot 3** and **Java 17**.  
The goal is to model a small airline booking platform, design the data model, expose REST APIs, and cover key business rules with tests.

---

###  Screenshots & Output

All project screenshots are included in the PDF below:

➡️ [Screenshots – All Word Doc.pdf](./Screenshots%20-%20All%20Word%20Doc.pdf)

---

### The Code Achieves

- Unit test coverage of **90%**
- Load testing executed using **JMeter** for **20, 50, 100 concurrent threads**
- Full flight inventory module — add, search, book, cancel tickets & flights across **9 endpoints**
- MongoDB Aggregation Pipelines for **7 analytical use cases**  
  *(located in `FlightInventoryRepository.java`, `AggregationsController.java`, `com.flightapp.aggregations` package)*
- Strong validation with proper status codes — including **201 for POST**
- Centralized exception handling via  
  `GlobalErrorHandler.java`, `ValidationException.java`, `ResourceNotFoundException.java`  
  *(package: `com.flightapp.exceptions`)*
- SonarQube analysis performed — reduced vulnerabilities from **87 → 14**
- Business logic reused from previous MVC-based project version
- Structured, layered, reactive architecture following clean coding practices

---

###  ER Diagram

![ER Diagram](./ER%20Diagram%20-%20FlightBookingSys.drawio.png)

---

### Built With

**Spring WebFlux**, **Reactive MongoDB**, **JUnit 5**, **Mockito**,  
**Jacoco**, **JMeter**, **SonarQube**, **Maven**, **Java 17**

---
