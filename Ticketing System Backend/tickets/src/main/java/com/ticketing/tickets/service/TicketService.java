package com.ticketing.tickets.service;

import com.ticketing.tickets.entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    Ticket saveTicket(Ticket ticket);
    List<Ticket> getAllTickets();
    Optional<Ticket> getTicketById(Long id);
    Ticket updateTicket(Long id, Ticket ticket);
    void deleteTicket(Long id);
}
