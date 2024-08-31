package com.ticketing.tickets.service;

import com.ticketing.tickets.entity.Event;

import java.util.List;


public interface EventService {
    Event saveEvent(Event event);
    List<Event> getAllEvents();
    Event getEventById(String id);
    Event updateEvent(Long id, Event event);
    void deleteEvent(Long id);
}
