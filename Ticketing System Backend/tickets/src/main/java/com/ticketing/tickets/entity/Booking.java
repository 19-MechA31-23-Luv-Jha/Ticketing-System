package com.ticketing.tickets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    @NotNull(message = "Ticket is required")
    private Ticket ticket;

    @NotNull(message = "User is required")
    @Size(min = 3, max = 100, message = "User name must be between 3 and 100 characters")
    private String user;

    private LocalDateTime bookingDate;
}