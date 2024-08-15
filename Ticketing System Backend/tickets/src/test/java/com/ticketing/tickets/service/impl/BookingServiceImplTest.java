package com.ticketing.tickets.service.impl;

import com.ticketing.tickets.entity.Booking;
import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceAlreadyExistsException;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.repository.BookingRepository;
import com.ticketing.tickets.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    BookingService bookingService = new BookingServiceImpl();


    @Test
    void testSaveBooking() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Booking booking = new Booking(1L, ticket, "John Doe", LocalDateTime.now());

        when(bookingRepository.findById(ticket.getId())).thenReturn(Optional.empty());
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking savedBooking = bookingService.saveBooking(booking);

        assertEquals(booking, savedBooking);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void testSaveBookingAlreadyExists() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Booking booking = new Booking(1L, ticket, "John Doe", LocalDateTime.now());

        when(bookingRepository.findById(ticket.getId())).thenReturn(Optional.of(booking));

        assertThrows(ResourceAlreadyExistsException.class, () -> bookingService.saveBooking(booking));
    }

    @Test
    void testGetAllBookings() {
        Ticket ticket1 = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Ticket ticket2 = new Ticket(2L, "Play", "B1", new BigDecimal("50.00"));
        Booking booking1 = new Booking(1L, ticket1, "John Doe", LocalDateTime.now());
        Booking booking2 = new Booking(2L, ticket2, "Jane Doe", LocalDateTime.now());
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> retrievedBookings = bookingService.getAllBookings();

        assertEquals(2, retrievedBookings.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBookingsNoBookings() {
        when(bookingRepository.findAll()).thenReturn(Collections.emptyList());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> bookingService.getAllBookings());
        assertEquals("No bookings found", thrown.getMessage());

        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testGetBookingById() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Booking booking = new Booking(1L, ticket, "John Doe", LocalDateTime.now());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Optional<Booking> retrievedBooking = bookingService.getBookingById(1L);

        assertTrue(retrievedBooking.isPresent());
        assertEquals(booking, retrievedBooking.get());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookingsByUser() {
        Ticket ticket1 = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Booking booking1 = new Booking(1L, ticket1, "John Doe", LocalDateTime.now());
        List<Booking> bookings = Arrays.asList(booking1);

        when(bookingRepository.findByUser("John Doe")).thenReturn(bookings);

        List<Booking> retrievedBookings = bookingService.getBookingsByUser("John Doe");

        assertEquals(1, retrievedBookings.size());
        verify(bookingRepository, times(1)).findByUser("John Doe");
    }

    @Test
    void testGetBookingsByUserNoBookings() {
        when(bookingRepository.findByUser("John Doe")).thenReturn(Collections.emptyList());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingsByUser("John Doe"));
        assertEquals("No bookings found for user: John Doe", thrown.getMessage());

        verify(bookingRepository, times(1)).findByUser("John Doe");
    }
}