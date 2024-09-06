package com.ticketing.tickets.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.tickets.entity.Ticket;
import com.ticketing.tickets.exception.ResourceNotFoundException;
import com.ticketing.tickets.repository.TicketRepository;
import com.ticketing.tickets.service.TicketService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final S3Client amazonS3;
    private static final String BUCKET_NAME = "my-op-bucket";
    private static final String TICKET_PREFIX = "tickets/Ticket_";
    private static final String JSON_EXTENSION = ".json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TicketServiceImpl(TicketRepository ticketRepository, S3Client amazonS3) {
        this.ticketRepository = ticketRepository;
        this.amazonS3 = amazonS3;
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        log.debug("Creating ticket: {}", ticket);
        Ticket savedTicket = ticketRepository.save(ticket);
        saveTicketToS3(savedTicket);
        return savedTicket;
    }

    // New method to save and update ticket in S3
    private void saveTicketToS3(Ticket ticket) {
        log.debug("Saving ticket to S3: {}", ticket);
        String key = TICKET_PREFIX + ticket.getId() + JSON_EXTENSION;
        try {
            // Serialize Ticket object to JSON string
            String jsonString = objectMapper.writeValueAsString(ticket);

            // Save JSON string to S3
            amazonS3.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(key)
                            .build(),
                    RequestBody.fromString(jsonString));
            log.info("Ticket saved to S3 with key: {}", key);
        } catch (S3Exception e) {
            log.error("Failed to save ticket to S3: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while saving ticket to S3: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> getAllTickets() {                                //updated
        log.debug("Fetching all tickets");
        List<Ticket> tickets =  ticketRepository.findAll();
        if (tickets.isEmpty()) {
            throw new ResourceNotFoundException("No tickets found");
        }
        return tickets;
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

        Ticket updatedTicket = ticketRepository.save(existingTicket);
        saveTicketToS3(updatedTicket);
        return updatedTicket;
    }

    @Override
    public void deleteTicket(Long id) {
        log.debug("Deleting ticket id: {}", id);
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(existingTicket);
    }

    // New method to get ticket from S3
    public Ticket getTicketFromS3(Long id) {
        log.debug("Getting ticket from S3 with id: {}", id);
        String key = TICKET_PREFIX + id + JSON_EXTENSION;
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();
            String ticketJson = amazonS3.getObjectAsBytes(getObjectRequest).asUtf8String();
            return objectMapper.readValue(ticketJson, Ticket.class);
        } catch (NoSuchKeyException e) {
            log.error("Ticket not found in S3 with id: {}", id);
            throw new ResourceNotFoundException("Ticket not found in S3 with id: " + id);
        } catch (S3Exception e) {
            log.error("Failed to get ticket from S3: {}", e.awsErrorDetails().errorMessage());
            throw new ResourceNotFoundException("Failed to get ticket from S3");
        } catch (Exception e) {
            log.error("An unexpected error occurred while getting ticket from S3: {}", e.getMessage(), e);
            throw new ResourceNotFoundException("Failed to get ticket from S3");
        }
    }

    // New method to get all tickets from S3
    public List<Ticket> getAllTicketsFromS3() {
        log.debug("Getting all tickets from S3");
        List<Ticket> tickets = new ArrayList<>();
        try {
            ListObjectsV2Request listObjects = ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .prefix("tickets/")
                    .build();
            ListObjectsV2Response listObjectsResponse = amazonS3.listObjectsV2(listObjects);

            for (S3Object s3Object : listObjectsResponse.contents()) {
                String key = s3Object.key();
                String ticketJson = amazonS3.getObjectAsBytes(GetObjectRequest.builder()
                                .bucket(BUCKET_NAME)
                                .key(key)
                                .build())
                        .asUtf8String();
                Ticket ticket = objectMapper.readValue(ticketJson, Ticket.class);
                tickets.add(ticket);
            }
            log.info("Fetched {} tickets from S3", tickets.size());
        } catch (S3Exception e) {
            log.error("Failed to get tickets from S3: {}", e.awsErrorDetails().errorMessage());
            throw new ResourceNotFoundException("Failed to list tickets from S3");
        } catch (Exception e) {
            log.error("An unexpected error occurred while deserializing tickets from S3: {}", e.getMessage(), e);
            throw new ResourceNotFoundException("Failed to deserialize tickets from S3");
        }
        return tickets;
    }


    // New method to delete ticket from S3
    public void deleteTicketFromS3(Long id) {
        log.debug("Deleting ticket from S3 with id: {}", id);
        String key = TICKET_PREFIX + id + JSON_EXTENSION;
        try {
            amazonS3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build());
            log.info("Ticket deleted from S3 with id: {}", id);
        } catch (NoSuchKeyException e) {
            log.error("Ticket not found in S3 with id: {}", id);
        } catch (S3Exception e) {
            log.error("Failed to delete ticket from S3: {}", e.awsErrorDetails().errorMessage());
        }catch (Exception e) {
            log.error("An unexpected error occurred while deleting ticket from S3: {}", e.getMessage(), e);
        }
    }
}