package com.ticketing.tickets.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotBlank(message = "Event name is mandatory")
    @Size(max = 100, message = "Event name should not exceed 100 characters")
    private String name;

    private Date date;

    @NotBlank(message = "Location is mandatory")
    @Size(max = 100, message = "Location should not exceed 100 characters")
    private String location;
}