package com.ticketing.tickets.service.impl;

import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;


    @Test
    void testSaveTicket() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket savedTicket = ticketService.saveTicket(ticket);

        assertNotNull(savedTicket);
        assertEquals(ticket, savedTicket);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testGetAllTickets() {
        Ticket ticket1 = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Ticket ticket2 = new Ticket(2L, "Play", "B1", new BigDecimal("50.00"));
        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);

        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> retrievedTickets = ticketService.getAllTickets();

        assertNotNull(retrievedTickets);
        assertEquals(2, retrievedTickets.size());
        assertEquals(ticket1.getEvent(), tickets.get(0).getEvent());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void testGetTicketById() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Optional<Ticket> retrievedTicket = ticketService.getTicketById(1L);

        assertTrue(retrievedTicket.isPresent());
        assertEquals(ticket, retrievedTicket.get());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateTicket() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Ticket updatedTicket = new Ticket(1L, "Concert Updated", "A2", new BigDecimal("150.00"));

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(updatedTicket);

        Ticket result = ticketService.updateTicket(1L, updatedTicket);

        assertEquals(updatedTicket.getEvent(), result.getEvent());
        assertEquals(updatedTicket.getSeat(), result.getSeat());
        assertEquals(updatedTicket.getPrice(), result.getPrice());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testDeleteTicket() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ticketService.deleteTicket(1L);

        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    void shouldThrowExceptionWhenNoTicketsFound() {
        when(ticketRepository.findAll()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.getAllTickets();
        });

        assertEquals("No tickets found", exception.getMessage());

        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTicket() {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));

        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.updateTicket(999L, ticket);
        });

        assertEquals("Ticket not found with id: 999", exception.getMessage());

        verify(ticketRepository, times(1)).findById(999L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.deleteTicket(999L);
        });

        assertEquals("Ticket not found with id: 999", exception.getMessage());

        verify(ticketRepository, times(1)).findById(999L);
    }

}