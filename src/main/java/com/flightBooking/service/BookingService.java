package com.flightBooking.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightBooking.model.Booking;
import com.flightBooking.model.BookingStatus;
import com.flightBooking.model.FlightInventory;
import com.flightBooking.model.Passenger;
import com.flightBooking.repository.BookingRepository;
import com.flightBooking.repository.FlightInventoryRepository;
import com.flightBooking.repository.PassengerRepository;
import com.flightBooking.request.BookingRequest;
import com.flightBooking.request.PassengerRequest;

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
	
	
	// to book a flight
	public Mono<Booking> bookFlight(String flightId, BookingRequest req) {
	    Mono<FlightInventory> outboundMono =
	            flightInventoryRepository.findById(flightId);

	    if (req.getReturnFlightId() == null) {
	        return outboundMono.flatMap(outbound ->
	                createBooking(req, outbound, null));
	    }

	    Mono<FlightInventory> returnMono =
	            flightInventoryRepository.findById(req.getReturnFlightId());

	    return outboundMono.zipWith(returnMono)
	            .flatMap(tuple ->
	                    createBooking(req, tuple.getT1(), tuple.getT2()));
	}
	
	
	// to create a booking
	private Mono<Booking> createBooking(
	        BookingRequest req,
	        FlightInventory outbound,
	        FlightInventory returning
	) {
	    Booking booking = new Booking();
	    booking.setOutboundFlightId(outbound.getFlightId());
	    booking.setReturnFlight(returning != null ? returning.getFlightId() : null);
	    booking.setContactName(req.getContactName());
	    booking.setContactEmail(req.getContactEmail());
	    booking.setTotalPassengers(req.getPassengers().size());
	    booking.setStatus(BookingStatus.CONFIRMED);
	    booking.setPnrOutbound(UUID.randomUUID().toString().replace("-", "").substring(0, 6));

	    return bookingRepository.save(booking)
	            .flatMap(saved -> savePassengers(req, saved).thenReturn(saved));
	}
	
	
	// to save passengers
	private Mono<Void> savePassengers(BookingRequest req, Booking booking) {
        Flux<Passenger> passengers = Flux.fromIterable(req.getPassengers())
                .map(p -> toPassenger(p, booking.getBookingId()));

        return passengerRepository.saveAll(passengers).then();
    }
	
	
	// to set passenger list
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
	
	
	// to get ticket with pnr
	public Mono<Booking> getTicket(String pnr) {
        return bookingRepository.findByPnrOutbound(pnr);
    }

	
	// to get passenger history by email
	public Flux<Booking> getHistory(String email) {
        return bookingRepository.findByContactEmail(email);
    }
	
	// to cancel a booking
	public Mono<Void> cancelTicket(String pnr) {
	    return bookingRepository.findByPnrOutbound(pnr)
	            .flatMap(booking ->
	                    flightInventoryRepository.findById(booking.getOutboundFlightId())
	                            .flatMap(inv -> {
	                                LocalDateTime departure = LocalDateTime.of(
	                                        inv.getDepartureDate(),
	                                        inv.getDepartureTime()
	                                );

	                                LocalDateTime cutoff = LocalDateTime.now().plusHours(24);

	                                if (departure.isBefore(cutoff)) {
	                                    return Mono.empty();
	                                }

	                                booking.setStatus(BookingStatus.CANCELLED);
	                                return bookingRepository.save(booking).then();
	                            })
	            );
	}

}
