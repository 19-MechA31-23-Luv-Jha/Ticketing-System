package com.ticketing.tickets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setEvent("Test Event");
        testTicket.setSeat("A1");
        testTicket.setPrice(BigDecimal.valueOf(100.00));
    }

    @Test
    void shouldCreateTicket() throws Exception {
        Ticket newTicket = new Ticket();
        newTicket.setEvent("New Event");
        newTicket.setSeat("B2");
        newTicket.setPrice(BigDecimal.valueOf(150.00));

        when(ticketService.saveTicket(any(Ticket.class))).thenReturn(newTicket);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTicket)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.event").value("New Event"))
                .andExpect(jsonPath("$.seat").value("B2"))
                .andExpect(jsonPath("$.price").value(150.00));
    }

    @Test
    void shouldGetAllTickets() throws Exception {
        when(ticketService.getAllTickets()).thenReturn(Arrays.asList(testTicket));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].event").value(testTicket.getEvent()))
                .andExpect(jsonPath("$[0].seat").value(testTicket.getSeat()))
                .andExpect(jsonPath("$[0].price").value(testTicket.getPrice().doubleValue()));
    }

    @Test
    void shouldGetTicketById() throws Exception {
        when(ticketService.getTicketById(anyLong())).thenReturn(Optional.of(testTicket));

        mockMvc.perform(get("/api/tickets/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event").value(testTicket.getEvent()))
                .andExpect(jsonPath("$.seat").value(testTicket.getSeat()))
                .andExpect(jsonPath("$.price").value(testTicket.getPrice().doubleValue()));
    }

    @Test
    void shouldUpdateTicket() throws Exception {
        testTicket.setEvent("Updated Event");

        when(ticketService.updateTicket(anyLong(), any(Ticket.class))).thenReturn(testTicket);

        mockMvc.perform(put("/api/tickets/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTicket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event").value("Updated Event"));
    }

    @Test
    void shouldDeleteTicket() throws Exception {
        Mockito.doNothing().when(ticketService).deleteTicket(anyLong());

        mockMvc.perform(delete("/api/tickets/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Ticket Deleted Successfully."));
    }

    @Test
    void shouldReturnNotFoundForNonExistentTicketId() throws Exception {
        when(ticketService.getTicketById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tickets/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found for this id :: 999"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentTicket() throws Exception {
        Ticket updatedTicketDetails = new Ticket();
        updatedTicketDetails.setEvent("Non-Existent Event");
        updatedTicketDetails.setSeat("D4");
        updatedTicketDetails.setPrice(BigDecimal.valueOf(250.00));

        when(ticketService.updateTicket(anyLong(), any(Ticket.class)))
                .thenThrow(new ResourceNotFoundException("Ticket not found with id: 999"));

        mockMvc.perform(put("/api/tickets/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTicketDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found with id: 999"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTicket() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Ticket not found with id: 999"))
                .when(ticketService).deleteTicket(anyLong());

        mockMvc.perform(delete("/api/tickets/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found with id: 999"));
    }

    @Test
    void shouldReturnBadRequestForInvalidTicketCreation() throws Exception {
        Ticket invalidTicket = new Ticket();
        invalidTicket.setEvent(""); // Invalid event
        invalidTicket.setSeat(""); // Invalid seat
        invalidTicket.setPrice(BigDecimal.ZERO); // Invalid price

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTicket)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.event").value("Event name is mandatory"))
                .andExpect(jsonPath("$.seat").value("Seat is mandatory"))
                .andExpect(jsonPath("$.price").value("Price must be greater than 0"));
    }

    @Test
    void shouldReturnBadRequestForInvalidTicketUpdate() throws Exception {
        Ticket invalidTicket = new Ticket();
        invalidTicket.setEvent(""); // Invalid event
        invalidTicket.setSeat(""); // Invalid seat
        invalidTicket.setPrice(BigDecimal.ZERO); // Invalid price

        mockMvc.perform(put("/api/tickets/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTicket)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.event").value("Event name is mandatory"))
                .andExpect(jsonPath("$.seat").value("Seat is mandatory"))
                .andExpect(jsonPath("$.price").value("Price must be greater than 0"));
    }

    // Tests for S3 endpoints

    @Test
    void shouldGetTicketByIdFromS3() throws Exception {
        when(ticketService.getTicketFromS3(anyLong())).thenReturn(testTicket);

        mockMvc.perform(get("/api/tickets/S3/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event").value(testTicket.getEvent()))
                .andExpect(jsonPath("$.seat").value(testTicket.getSeat()))
                .andExpect(jsonPath("$.price").value(testTicket.getPrice().doubleValue()));
    }

    @Test
    void shouldGetAllTicketsFromS3() throws Exception {
        when(ticketService.getAllTicketsFromS3()).thenReturn(Arrays.asList(testTicket));

        mockMvc.perform(get("/api/tickets/S3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].event").value(testTicket.getEvent()))
                .andExpect(jsonPath("$[0].seat").value(testTicket.getSeat()))
                .andExpect(jsonPath("$[0].price").value(testTicket.getPrice().doubleValue()));
    }

    @Test
    void shouldDeleteTicketFromS3() throws Exception {
        Mockito.doNothing().when(ticketService).deleteTicketFromS3(anyLong());

        mockMvc.perform(delete("/api/tickets/S3/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Ticket Deleted Successfully From S3."));
    }

    @Test
    void shouldReturnNotFoundForNonExistentTicketIdFromS3() throws Exception {
        when(ticketService.getTicketFromS3(anyLong())).thenThrow(new ResourceNotFoundException("Ticket not found in S3 with id: 999"));

        mockMvc.perform(get("/api/tickets/S3/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found in S3 with id: 999"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTicketFromS3() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Ticket not found in S3 with id: 999"))
                .when(ticketService).deleteTicketFromS3(anyLong());

        mockMvc.perform(delete("/api/tickets/S3/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found in S3 with id: 999"));
    }
}
