import { Component, OnInit } from '@angular/core';
import { Ticket } from '../../data-types';
import { TicketService } from '../../services/ticket.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [FormsModule,CommonModule],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.css'
})
export class TicketsComponent implements OnInit {
  tickets: Ticket[] = [];
  newTicket: Ticket = {
    id: 0,
    event: '',
    seat: '',
    price: 0
  };
  editingTicketId: number | null = null;

  constructor(private ticketService: TicketService) { }

  ngOnInit(): void {
    this.loadTickets();
  }

  loadTickets(): void {
    this.ticketService.getAllTickets().subscribe({
      next: (tickets) => {
        this.tickets = tickets;
      },
      error: (err) => {
        console.error('Error loading tickets', err);
        alert('Failed to load tickets. Please try again.');
      }
    });
  }

  addTicket(): void {
    this.ticketService.addTicket(this.newTicket).subscribe({
      next: (ticket) => {
        this.tickets.push(ticket);
        this.newTicket = {
          id: 0,
          event: '',
          seat: '',
          price: 0
        };
        alert('Ticket Added Successfully!');
      },
      error: (err) => {
        console.error('Error adding ticket', err);
        alert('Failed to add ticket. Please try again.');
      }
    });
  }

  deleteTicket(id: number): void {
    this.ticketService.deleteTicket(id).subscribe({
      next: (message) => {
        // Refetch the tickets to ensure the list is up-to-date
        this.loadTickets();
        alert(message);
      },
      error: (err) => {
        console.error('Error deleting ticket', err);
        alert('Failed to delete ticket. Please try again.');
      }
    });
  }

  startEditing(ticketId: number): void {
    this.editingTicketId = ticketId;
  }

  saveTicket(ticket: Ticket): void {
    this.ticketService.updateTicket(ticket.id, ticket).subscribe({
      next: (updatedTicket) => {
        const index = this.tickets.findIndex(t => t.id === updatedTicket.id);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }
        this.editingTicketId = null;
        alert('Ticket Updated Successfully!');
      },
      error: (err) => {
        console.error('Error updating ticket', err);
        alert('Failed to update ticket. Please try again.');
      }
    });
  }

  cancelEditing(): void {
    this.editingTicketId = null;
  }
}