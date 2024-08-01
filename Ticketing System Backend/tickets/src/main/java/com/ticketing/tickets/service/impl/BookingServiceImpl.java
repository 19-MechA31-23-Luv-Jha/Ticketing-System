package com.ticketing.tickets.service.impl;


import com.ticketing.tickets.entity.Booking;
import com.ticketing.tickets.exception.ResourceAlreadyExistsException;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.repository.BookingRepository;
import com.ticketing.tickets.service.BookingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public Booking saveBooking(Booking booking) {
        log.debug("Creating Booking: {}", booking);
        Optional<Booking> existingBooking = bookingRepository.findById(booking.getTicket().getId());
        if (existingBooking.isPresent()) {
            throw new ResourceAlreadyExistsException("Booking already exists for user: " + booking.getUser() + " and ticket ID: " + booking.getTicket().getId());
        }
        booking.setBookingDate(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        log.debug("Fetching all bookings");
        List<Booking> bookings =  bookingRepository.findAll();
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found");
        }
        return bookings;
    }

    public Optional<Booking> getBookingById(Long bookingId) {
        log.debug("Fetching booking by id: {}", bookingId);
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getBookingsByUser(String user) {
        log.debug("Fetching bookings of user: {}", user);
        List<Booking> bookings = bookingRepository.findByUser(user);
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found for user: " + user);
        }
        return bookings;
    }
}