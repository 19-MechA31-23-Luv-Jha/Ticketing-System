package com.ticketing.tickets.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

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

    @Mock
    private S3Client amazonS3;

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
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
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
        assertEquals(ticket1.getEvent(), retrievedTickets.get(0).getEvent());
        assertEquals(ticket2.getEvent(), retrievedTickets.get(1).getEvent());
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
        Ticket existingTicket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Ticket updatedTicket = new Ticket(1L, "Concert Updated", "A2", new BigDecimal("150.00"));

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.save(existingTicket)).thenReturn(updatedTicket);

        Ticket result = ticketService.updateTicket(1L, updatedTicket);

        assertEquals(updatedTicket.getEvent(), result.getEvent());
        assertEquals(updatedTicket.getSeat(), result.getSeat());
        assertEquals(updatedTicket.getPrice(), result.getPrice());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(existingTicket);
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
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

    @Test
    void testGetTicketFromS3() throws Exception {
        Ticket ticket = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        ObjectMapper objectMapper = new ObjectMapper();
        String ticketJson = objectMapper.writeValueAsString(ticket);
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), ticketJson.getBytes());

        when(amazonS3.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        Ticket retrievedTicket = ticketService.getTicketFromS3(1L);

        assertNotNull(retrievedTicket);
        assertEquals(ticket.getEvent(), retrievedTicket.getEvent());
        assertEquals(ticket.getSeat(), retrievedTicket.getSeat());
        assertEquals(ticket.getPrice(), retrievedTicket.getPrice());
        verify(amazonS3, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenGettingTicketFromS3NotFound() {
        when(amazonS3.getObjectAsBytes(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.class);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.getTicketFromS3(1L);
        });

        assertEquals("Ticket not found in S3 with id: 1", exception.getMessage());
        verify(amazonS3, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    void testGetAllTicketsFromS3() throws Exception {
        Ticket ticket1 = new Ticket(1L, "Concert", "A1", new BigDecimal("100.00"));
        Ticket ticket2 = new Ticket(2L, "Play", "B1", new BigDecimal("50.00"));
        ObjectMapper objectMapper = new ObjectMapper();
        String ticketJson1 = objectMapper.writeValueAsString(ticket1);
        String ticketJson2 = objectMapper.writeValueAsString(ticket2);
        ResponseBytes<GetObjectResponse> responseBytes1 = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), ticketJson1.getBytes());
        ResponseBytes<GetObjectResponse> responseBytes2 = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), ticketJson2.getBytes());

        List<S3Object> s3Objects = Arrays.asList(
                S3Object.builder().key("tickets/Ticket_1.json").build(),
                S3Object.builder().key("tickets/Ticket_2.json").build()
        );
        ListObjectsV2Response listObjectsResponse = ListObjectsV2Response.builder().contents(s3Objects).build();

        when(amazonS3.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsResponse);
        when(amazonS3.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(responseBytes1)
                .thenReturn(responseBytes2);

        List<Ticket> retrievedTickets = ticketService.getAllTicketsFromS3();

        assertNotNull(retrievedTickets);
        assertEquals(2, retrievedTickets.size());
        assertEquals(ticket1.getEvent(), retrievedTickets.get(0).getEvent());
        assertEquals(ticket2.getEvent(), retrievedTickets.get(1).getEvent());
        verify(amazonS3, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
        verify(amazonS3, times(2)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenGettingAllTicketsFromS3Fails() {
        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                        .errorMessage("Failed to list tickets from S3")
                        .build())
                .build();

        when(amazonS3.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(s3Exception);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.getAllTicketsFromS3();
        });

        assertEquals("Failed to list tickets from S3", exception.getMessage());
        verify(amazonS3, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @Test
    void testDeleteTicketFromS3() {
        DeleteObjectResponse deleteObjectResponse = DeleteObjectResponse.builder().build();
        when(amazonS3.deleteObject(any(DeleteObjectRequest.class))).thenReturn(deleteObjectResponse);

        ticketService.deleteTicketFromS3(1L);

        verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
