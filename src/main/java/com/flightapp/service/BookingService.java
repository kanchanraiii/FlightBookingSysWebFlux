package com.flightapp.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.Passenger;
import com.flightapp.model.TripType;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightInventoryRepository flightInventoryRepository;

    @Autowired
    private PassengerRepository passengerRepository;


    // book a flight
    public Mono<Booking> bookFlight(String flightId, BookingRequest req) {

        validatePassengersExist(req);
        validateTripType(req);

        Mono<FlightInventory> outboundMono =
                flightInventoryRepository.findById(flightId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Outbound flight not found")));

        if (req.getReturnFlightId() == null) {
            return outboundMono.flatMap(outbound -> {
                validateSeatAvailability(outbound, req.getPassengers().size());
                validateEachPassenger(req, false);
                return createBooking(req, outbound, null);
            });
        }

        Mono<FlightInventory> returnMono =
                flightInventoryRepository.findById(req.getReturnFlightId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Return flight not found")));

        return outboundMono.zipWith(returnMono)
                .flatMap(tuple -> {
                    validateSeatAvailability(tuple.getT1(), req.getPassengers().size());
                    validateSeatAvailability(tuple.getT2(), req.getPassengers().size());
                    validateEachPassenger(req, true);
                    return createBooking(req, tuple.getT1(), tuple.getT2());
                });
    }


    // create bookings
    private Mono<Booking> createBooking(
            BookingRequest req,
            FlightInventory outbound,
            FlightInventory returning
    ) {
        Booking booking = new Booking();
        booking.setOutboundFlightId(outbound.getFlightId());
        booking.setReturnFlight(returning != null ? returning.getFlightId() : null);
        booking.setTripType(req.getTripType());
        booking.setContactName(req.getContactName());
        booking.setContactEmail(req.getContactEmail());
        booking.setTotalPassengers(req.getPassengers().size());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPnrOutbound(UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase());

        return bookingRepository.save(booking)
                .flatMap(saved -> updateSeats(outbound, returning, req.getPassengers().size())
                        .then(savePassengers(req, saved))
                        .thenReturn(saved));
    }


    // save passenger list
    private Mono<Void> savePassengers(BookingRequest req, Booking booking) {
        Flux<Passenger> passengers = Flux.fromIterable(req.getPassengers())
                .map(p -> toPassenger(p, booking.getBookingId()));

        return passengerRepository.saveAll(passengers).then();
    }


    private Passenger toPassenger(PassengerRequest req, String bookingId) {
        Passenger p = new Passenger();
        p.setName(req.getName());
        p.setAge(req.getAge());
        p.setGender(req.getGender());
        p.setMeal(req.getMeal());
        p.setSeatOutbound(req.getSeatOutbound());
        p.setSeatReturn(req.getSeatReturn());
        p.setBookingId(bookingId);
        return p;
    }


    // reduce seats
    private Mono<Void> updateSeats(FlightInventory outbound, FlightInventory returning, int count) {
        outbound.setAvailableSeats(outbound.getAvailableSeats() - count);
        Mono<FlightInventory> saveOutbound = flightInventoryRepository.save(outbound);

        if (returning != null) {
            returning.setAvailableSeats(returning.getAvailableSeats() - count);
            Mono<FlightInventory> saveReturn = flightInventoryRepository.save(returning);
            return Mono.when(saveOutbound, saveReturn).then();
        }

        return saveOutbound.then();
    }


    // get ticket using PNR
    public Mono<Booking> getTicket(String pnr) {
        return bookingRepository.findByPnrOutbound(pnr)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PNR not found")));
    }


    // get booking history by email
    public Flux<Booking> getHistory(String email) {
        return bookingRepository.findByContactEmail(email);
    }


    // cancel booking only if departure > 24 hours & restore seats
    public Mono<Void> cancelTicket(String pnr) {
        return bookingRepository.findByPnrOutbound(pnr)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PNR not found")))
                .flatMap(booking ->
                        flightInventoryRepository.findById(booking.getOutboundFlightId())
                                .flatMap(inv -> {
                                    LocalDateTime departure = LocalDateTime.of(
                                            inv.getDepartureDate(),
                                            inv.getDepartureTime()
                                    );

                                    LocalDateTime cutoff = LocalDateTime.now().plusHours(24);

                                    if (departure.isBefore(cutoff)) {
                                        return Mono.error(new ValidationException("Cannot cancel within 24 hours of departure"));
                                    }

                                    inv.setAvailableSeats(inv.getAvailableSeats() + booking.getTotalPassengers());

                                    return flightInventoryRepository.save(inv)
                                            .then(restoreReturnFlightSeats(booking))
                                            .then(Mono.defer(() -> {
                                                booking.setStatus(BookingStatus.CANCELLED);
                                                return bookingRepository.save(booking).then();
                                            }));
                                })
                );
    }


    private Mono<Void> restoreReturnFlightSeats(Booking booking) {
        if (booking.getReturnFlight() == null) return Mono.empty();

        return flightInventoryRepository.findById(booking.getReturnFlight())
                .flatMap(ret -> {
                    ret.setAvailableSeats(ret.getAvailableSeats() + booking.getTotalPassengers());
                    return flightInventoryRepository.save(ret).then();
                });
    }


    //helper functions for validations
    private void validatePassengersExist(BookingRequest req) {
        if (req.getPassengers() == null || req.getPassengers().isEmpty()) {
            throw new ValidationException("At least one passenger is required");
        }
    }

    private void validateTripType(BookingRequest req) {
        if (req.getTripType() == null) {
            throw new ValidationException("Trip Type is required");
        }
        if (req.getTripType() == TripType.ROUND_TRIP && req.getReturnFlightId() == null) {
            throw new ValidationException("Return flight ID is required for round-trip");
        }
    }

    private void validateSeatAvailability(FlightInventory flight, int passengerCount) {
        if (flight.getAvailableSeats() < passengerCount) {
            throw new ValidationException("Not enough seats available");
        }
    }

    private void validateEachPassenger(BookingRequest req, boolean isRoundTrip) {
        req.getPassengers().forEach(p -> validatePassenger(p, isRoundTrip));
    }

    private void validatePassenger(PassengerRequest p, boolean isRoundTrip) {
        if (p.getAge() <= 0) {
            throw new ValidationException("Passenger age must be greater than 0");
        }
        if (p.getSeatOutbound() == null || p.getSeatOutbound().isBlank()) {
            throw new ValidationException("Outbound seat is required");
        }
        if (isRoundTrip && (p.getSeatReturn() == null || p.getSeatReturn().isBlank())) {
            throw new ValidationException("Return seat is required for round-trip");
        }
    }
}
