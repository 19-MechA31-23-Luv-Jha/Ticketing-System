package com.ticketing.tickets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.tickets.entity.Booking;
import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.repository.BookingRepository;
import com.ticketing.tickets.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Ticket testTicket;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        ticketRepository.deleteAll();

        testTicket = new Ticket();
        testTicket.setEvent("Test Event");
        testTicket.setSeat("A1");
        testTicket.setPrice(BigDecimal.valueOf(100.00));
        testTicket = ticketRepository.save(testTicket);

        testBooking = new Booking();
        testBooking.setTicket(testTicket);
        testBooking.setUser("testUser");
        testBooking = bookingRepository.save(testBooking);
    }

    @Test
    void shouldCreateBooking() throws Exception {
        Ticket newTicket = new Ticket();
        newTicket.setEvent("New Event");
        newTicket.setSeat("B2");
        newTicket.setPrice(BigDecimal.valueOf(150.00));
        newTicket = ticketRepository.save(newTicket);

        Booking newBooking = new Booking();
        newBooking.setTicket(newTicket);
        newBooking.setUser("newUser");

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user").value("newUser"))
                .andExpect(jsonPath("$.ticket.event").value("New Event"))
                .andExpect(jsonPath("$.ticket.seat").value("B2"));
    }

    @Test
    void shouldGetAllBookings() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user").value(testBooking.getUser()))
                .andExpect(jsonPath("$[0].ticket.event").value(testTicket.getEvent()));
    }

    @Test
    void shouldGetBookingById() throws Exception {
        mockMvc.perform(get("/api/bookings/{id}", testBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value(testBooking.getUser()))
                .andExpect(jsonPath("$.ticket.event").value(testTicket.getEvent()));
    }

    @Test
    void shouldGetBookingsByUser() throws Exception {
        mockMvc.perform(get("/api/bookings/user/{user}", testBooking.getUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user").value(testBooking.getUser()))
                .andExpect(jsonPath("$[0].ticket.event").value(testTicket.getEvent()));
    }

    @Test
    void shouldReturnNotFoundForInvalidBookingId() throws Exception {
        mockMvc.perform(get("/api/bookings/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found for this id :: 999"));
    }

    @Test
    void shouldReturnNotFoundForEmptyBookingList() throws Exception {
        bookingRepository.deleteAll(); // Ensures no bookings exist
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No bookings found"));
    }

    @Test
    void shouldReturnNotFoundForBookingsByUserWithNoBookings() throws Exception {
        mockMvc.perform(get("/api/bookings/user/{user}", "nonExistentUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No bookings found for user: nonExistentUser"));
    }

    @Test
    void shouldReturnConflictWhenCreatingDuplicateBooking() throws Exception {
        Booking duplicateBooking = new Booking();
        duplicateBooking.setTicket(testTicket);
        duplicateBooking.setUser("testUser");  // Already exists

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateBooking)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Booking already exists for user: testUser and ticket ID: " + testTicket.getId()));
    }
}

