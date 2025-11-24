package com.flightBooking.service;

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
	private BookingRepository BookingRepository;
	
	@Autowired
	private FlightInventoryRepository FlightInventoryRepository;
	
	@Autowired
	private PassengerRepository PassengerRepository;
	
	
	// to book a flight
	public Mono<Booking> bookFlight(BookingRequest req) {
	    Mono<FlightInventory> outboundMono =
	            FlightInventoryRepository.findById(req.getOutboundFlightId());

	    if (req.getReturnFlightId() == null) {
	        return outboundMono.flatMap(outbound ->
	                createBooking(req, outbound, null));
	    }

	    Mono<FlightInventory> returnMono =
	            FlightInventoryRepository.findById(req.getReturnFlightId());

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

	    return BookingRepository.save(booking)
	            .flatMap(saved -> savePassengers(req, saved)
	                    .thenReturn(saved));
	}
	
	
	// to save passengers
	private Mono<Void> savePassengers(BookingRequest req, Booking booking) {
        Flux<Passenger> passengers = Flux.fromIterable(req.getPassengers())
                .map(p -> toPassenger(p, booking.getBookingId()));

        return PassengerRepository.saveAll(passengers).then();
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
	
	// updating seat no
	@SuppressWarnings("unused")
	private Mono<Void> updateSeats(FlightInventory outbound, FlightInventory returning, int count) {
        outbound.setAvailableSeats(outbound.getAvailableSeats() - count);
        Mono<FlightInventory> saveOutbound = FlightInventoryRepository.save(outbound);

        if (returning != null) {
            returning.setAvailableSeats(returning.getAvailableSeats() - count);
            Mono<FlightInventory> saveReturn = FlightInventoryRepository.save(returning);
            return Mono.when(saveOutbound, saveReturn).then();
        }

        return saveOutbound.then();
    }

    // get ticker by pnr
	public Mono<Booking> getTicket(String pnr) {
        return BookingRepository.findByPnrOutbound(pnr);
    }

    // get history by email
	public Flux<Booking> getHistory(String email) {
        return BookingRepository.findByContactEmail(email);
    }



}
