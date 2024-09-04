package com.ticketing.tickets.controller;


import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.service.TicketService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Log4j2
@CrossOrigin(origins = "http://localhost:4200")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Validated @RequestBody Ticket ticket) {
        log.debug("Request to create ticket: {}", ticket);
        return new ResponseEntity<>(ticketService.saveTicket(ticket), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        log.debug("Request to get all tickets");
        return new ResponseEntity<>(ticketService.getAllTickets(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable(value = "id") Long ticketId)
            throws ResourceNotFoundException {
        log.debug("Request to get ticket by id: {}", ticketId);
        Ticket ticket = ticketService.getTicketById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for this id :: " + ticketId));
        return new ResponseEntity<>(ticket,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable(value = "id") Long ticketId,
                                               @Validated @RequestBody Ticket ticketDetails) throws ResourceNotFoundException {
        log.debug("Request to update ticket id: {}", ticketId);
        Ticket updatedTicket = ticketService.updateTicket(ticketId, ticketDetails);
        return new ResponseEntity<>(updatedTicket,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable(value = "id") Long ticketId) throws ResourceNotFoundException {
        log.debug("Request to delete ticket id: {}", ticketId);
        ticketService.deleteTicket(ticketId);
        return new ResponseEntity<>("Ticket Deleted Successfully.",HttpStatus.OK);
    }

    @GetMapping("/S3/{id}")
    public ResponseEntity<Ticket> getTicketByIdFromS3(@PathVariable(value = "id") Long ticketId)
            throws ResourceNotFoundException {
        log.debug("Request to get ticket by id from S3: {}", ticketId);
        Ticket ticket = ticketService.getTicketFromS3(ticketId);
        return new ResponseEntity<>(ticket,HttpStatus.OK);
    }

    @GetMapping("/S3")
    public ResponseEntity<List<Ticket>> getAllTicketsFromS3() {
        log.debug("Request to get all tickets From S3");
        return new ResponseEntity<>(ticketService.getAllTicketsFromS3(),HttpStatus.OK);
    }

    @DeleteMapping("S3/{id}")
    public ResponseEntity<String> deleteTicketFromS3(@PathVariable(value = "id") Long ticketId) throws ResourceNotFoundException {
        log.debug("Request to delete ticket id From S3: {}", ticketId);
        ticketService.deleteTicketFromS3(ticketId);
        return new ResponseEntity<>("Ticket Deleted Successfully From S3.",HttpStatus.OK);
    }
}
