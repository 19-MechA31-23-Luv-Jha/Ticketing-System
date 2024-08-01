import { Component, OnInit } from '@angular/core';
import { TicketService } from '../../services/ticket.service';
import { Booking, Ticket } from '../../data-types';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-book',
  standalone: true,
  imports: [RouterModule,FormsModule,CommonModule],
  templateUrl: './book.component.html',
  styleUrl: './book.component.css'
})
export class BookComponent implements OnInit {
  tickets: Ticket[] = [];
  user: string = '';
  selectedTicket: Ticket | null = null;

  constructor(private ticketService: TicketService, private bookingService: BookingService, private router: Router) { }

  ngOnInit(): void {
    this.loadTickets();
  }

  loadTickets(): void {
    this.ticketService.getAllTickets().subscribe(tickets => {
      this.tickets = tickets;
    });
  }

  showBookingForm(ticket: Ticket): void {
    this.selectedTicket = ticket;
  }

  bookTicket(): void {
    if (!this.user || !this.selectedTicket) {
      alert('Please provide user information.');
      return;
    }

    const booking: Booking = {
      id: 0,
      ticket: this.selectedTicket,
      user: this.user,
      bookingDate: new Date().toISOString()
    };

    this.bookingService.createBooking(booking).subscribe(
      response => {
        alert('Booking done successfully!');
        this.selectedTicket = null;
        this.user = '';
        this.loadTickets();
      },
      error => {
        if (error.status === 409) {
          alert('Booking already exists for this user and ticket.');
        } else {
          console.error('Error creating booking:', error);
          alert('Failed to book ticket. Please try again.');
        }
      }
    );
  }

  cancelBooking(): void {
    this.selectedTicket = null;
    this.user = '';
  }
}