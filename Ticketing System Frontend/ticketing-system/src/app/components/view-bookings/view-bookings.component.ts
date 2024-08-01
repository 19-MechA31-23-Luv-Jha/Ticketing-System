import { Component } from '@angular/core';
import { Booking } from '../../data-types';
import { BookingService } from '../../services/booking.service';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-view-bookings',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './view-bookings.component.html',
  styleUrl: './view-bookings.component.css'
})
export class ViewBookingsComponent {
  bookings: Booking[] = [];
  filteredBookings: Booking[] = [];
  filterUser: string = '';

  constructor(private bookingService: BookingService) { }

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.bookingService.getAllBookings().subscribe(bookings => {
      this.bookings = bookings;
      this.filteredBookings = bookings;
    });
  }

  filterBookings(): void {
    this.filteredBookings = this.bookings.filter(booking =>
      (this.filterUser === '' || booking.user.includes(this.filterUser)) 
    );
  }

  clearFilters(): void {
    this.filterUser = '';
    this.filteredBookings = this.bookings;
  }
}
