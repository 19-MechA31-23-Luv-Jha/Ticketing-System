package com.ticketing.tickets.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.ticketing.tickets.entity.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EventServiceImpl eventService ;

    private Event testEvent;

    private static final String MOCK_API_URL = "https://66be4c7774dfc195586f1cc1.mockapi.io/api/events";

    @BeforeEach
    void setUp() {
        // Initialize a test event
        testEvent = new Event();
        testEvent.setId("1");
        testEvent.setName("Test Event");
        testEvent.setLocation("Test Location");
        testEvent.setDate(new Date());
    }

    @Test
    void testSaveEvent() {
        when(restTemplate.postForObject(anyString(), any(Event.class), eq(Event.class))).thenReturn(testEvent);

        Event savedEvent = eventService.saveEvent(testEvent);

        assertNotNull(savedEvent);
        assertEquals(testEvent, savedEvent);
        verify(restTemplate, times(1)).postForObject(eq(MOCK_API_URL), eq(testEvent), eq(Event.class));
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = List.of(testEvent);
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(events);

        List<Event> retrievedEvents = eventService.getAllEvents();

        assertEquals(events, retrievedEvents);
        verify(restTemplate, times(1)).getForObject(eq(MOCK_API_URL), eq(List.class));
    }

    @Test
    void testGetEventById() {
        when(restTemplate.getForObject(anyString(), eq(Event.class))).thenReturn(testEvent);

        Event retrievedEvent = eventService.getEventById("1");

        assertEquals(testEvent, retrievedEvent);
        verify(restTemplate, times(1)).getForObject(eq(MOCK_API_URL + "/1"), eq(Event.class));
    }

    @Test
    void testUpdateEvent() {
        when(restTemplate.getForObject(anyString(), eq(Event.class))).thenReturn(testEvent);

        Event updatedEvent = eventService.updateEvent(1L, testEvent);

        assertEquals(testEvent, updatedEvent);
        verify(restTemplate, times(2)).getForObject(eq(MOCK_API_URL + "/1"), eq(Event.class));
        verify(restTemplate, times(1)).put(eq(MOCK_API_URL + "/1"), eq(testEvent));
    }

    @Test
    void testUpdateEvent_NotFound() {
        when(restTemplate.getForObject(anyString(), eq(Event.class))).thenReturn(null);

        assertThrows(NullPointerException.class, () -> eventService.updateEvent(1L, testEvent));

        verify(restTemplate, times(1)).getForObject(eq(MOCK_API_URL + "/1"), eq(Event.class));
        verify(restTemplate, never()).put(anyString(), any(Event.class));
    }

    @Test
    void testDeleteEvent() {
        doNothing().when(restTemplate).delete(anyString());

        eventService.deleteEvent(1L);

        verify(restTemplate, times(1)).delete(eq(MOCK_API_URL + "/1"));
    }


}