import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Booking } from '../data-types';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  
  private baseUrl = 'http://localhost:8585/tickets/api/bookings'; 

  constructor(private http: HttpClient) { }

  createBooking(booking: Booking): Observable<Booking> {
    return this.http.post<Booking>(this.baseUrl, booking);
  }

  getAllBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.baseUrl);
  }

  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.baseUrl}/${id}`);
  }

  getBookingsByUser(user: string): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.baseUrl}/user/${user}`);
  }
}
