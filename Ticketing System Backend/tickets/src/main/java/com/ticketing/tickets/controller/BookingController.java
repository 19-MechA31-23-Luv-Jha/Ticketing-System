package com.ticketing.tickets.controller;

import com.ticketing.tickets.entity.Booking;

import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.service.BookingService;
import com.ticketing.tickets.service.TicketService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Log4j2
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Validated @RequestBody Booking booking) {
        log.debug("Request to create booking: {}", booking);
        Ticket ticket = ticketService.getTicketById(booking.getTicket().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for this id :: " + booking.getTicket().getId()));
        booking.setTicket(ticket);
        Booking savedBooking = bookingService.saveBooking(booking);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        log.debug("Request to get all bookings");
        return new ResponseEntity<>(bookingService.getAllBookings(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable(value = "id") Long bookingId) {
        log.debug("Request to get booking by id: {}", bookingId);
        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for this id :: " + bookingId));
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/user/{user}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable String user) {
        log.debug("Request to get bookings by user: {}", user);
        List<Booking> bookings = bookingService.getBookingsByUser(user);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

}