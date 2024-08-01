package com.ticketing.tickets.service;

import com.ticketing.tickets.entity.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking saveBooking(Booking booking);
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(Long bookingId);
    List<Booking> getBookingsByUser(String user);

}
