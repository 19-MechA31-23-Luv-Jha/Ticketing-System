package com.ticketing.tickets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.tickets.entity.Event;
import com.ticketing.tickets.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    private Event event;

    @BeforeEach
    public void setup() {
        event = new Event();
        event.setName("Test Event");
        event.setLocation("Test Location");
        event.setDate(new Date());
    }

    @Test
    void testCreateEvent() throws Exception {
        when(eventService.saveEvent(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    @Test
    void testGetAllEvents() throws Exception {
        when(eventService.getAllEvents()).thenReturn(Arrays.asList(event));

        mockMvc.perform(get("/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Event"));
    }

    @Test
    void testGetEventById() throws Exception {
        when(eventService.getEventById(anyString())).thenReturn(event);

        mockMvc.perform(get("/events/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void testUpdateEvent() throws Exception {
        event.setName("Updated Event");
        when(eventService.updateEvent(anyLong(), any(Event.class))).thenReturn(event);

        mockMvc.perform(put("/events/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        Mockito.doNothing().when(eventService).deleteEvent(anyLong());

        mockMvc.perform(delete("/events/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Event Deleted Successfully."));
    }
}