import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { TicketsComponent } from './components/tickets/tickets.component';
import { BookComponent } from './components/book/book.component';
import { ViewBookingsComponent } from './components/view-bookings/view-bookings.component';


export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'home', component: HomeComponent },
    { path: 'tickets', component: TicketsComponent },
    { path: 'book', component: BookComponent },
    { path: 'bookings', component: ViewBookingsComponent },
    { path: '**', redirectTo: 'home' }
];
