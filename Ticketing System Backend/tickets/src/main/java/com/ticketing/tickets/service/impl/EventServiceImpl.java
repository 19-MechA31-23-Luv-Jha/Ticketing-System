package com.ticketing.tickets.service.impl;

import com.ticketing.tickets.entity.Event;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.service.EventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class EventServiceImpl implements EventService {

    private RestTemplate restTemplate;

    public EventServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String MOCK_API_URL = "https://66be4c7774dfc195586f1cc1.mockapi.io/api/events";
    private static final String MOCK_API_RESPONSE_LOG = "MockAPI Response: {}";

    @Override
    public Event saveEvent(Event event) {
        log.debug("Creating event: {}", event);
        Date currentDate = new Date();
        event.setDate(currentDate);
        Event mockApiResponse = restTemplate.postForObject(MOCK_API_URL, event, Event.class);
        log.debug(MOCK_API_RESPONSE_LOG, mockApiResponse);
        return mockApiResponse;
    }

    @Override
    public List<Event> getAllEvents() {
        log.debug("Fetching all events");
        List<Event>  mockApiResponse = restTemplate.getForObject(MOCK_API_URL,List.class);
        log.debug(MOCK_API_RESPONSE_LOG, (Object) mockApiResponse);
        return mockApiResponse;
    }

    @Override
    public Event getEventById(String id) {
        log.debug("Fetching event by id: {}", id);
        Event mockApiResponse = restTemplate.getForObject(MOCK_API_URL + "/" + id, Event.class);
        log.debug(MOCK_API_RESPONSE_LOG, mockApiResponse);
        return mockApiResponse;
    }

    @Override
    public Event updateEvent(Long id, Event event) {
        log.debug("Updating event id: {}", id);
        String url = MOCK_API_URL + "/" + id;

        Event existingEvent = restTemplate.getForObject(url, Event.class);

        Date currentDate = new Date();
        existingEvent.setDate(currentDate);
        existingEvent.setName(event.getName());
        existingEvent.setLocation(event.getLocation());
        restTemplate.put(url, existingEvent);

        Event updatedEvent = restTemplate.getForObject(url, Event.class);
        log.debug(MOCK_API_RESPONSE_LOG, updatedEvent);
        return updatedEvent;
    }

    @Override
    public void deleteEvent(Long id) {
        log.debug("Deleting event id: {}", id);
        String url = MOCK_API_URL + "/" + id;
        restTemplate.delete(url);
        log.debug("MockAPI Response: Event deleted");

    }
}
