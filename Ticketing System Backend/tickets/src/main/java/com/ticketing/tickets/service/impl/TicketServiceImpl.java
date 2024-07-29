package com.ticketing.tickets.service.impl;

import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.repository.TicketRepository;
import com.ticketing.tickets.service.TicketService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public Ticket saveTicket(Ticket ticket) {
        log.debug("Creating ticket: {}", ticket);
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> getAllTickets() {
        log.debug("Fetching all tickets");
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        log.debug("Fetching ticket by id: {}", id);
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket updateTicket(Long id, Ticket ticket) {
        log.debug("Updating ticket id: {}", id);
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        existingTicket.setEvent(ticket.getEvent());
        existingTicket.setSeat(ticket.getSeat());
        existingTicket.setPrice(ticket.getPrice());
        return ticketRepository.save(existingTicket);
    }

    @Override
    public void deleteTicket(Long id) {
        log.debug("Deleting ticket id: {}", id);
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(existingTicket);
    }
}