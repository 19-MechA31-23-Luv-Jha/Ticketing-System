export interface Ticket {
    id: number;
    event: string;
    seat: string;
    price: number;
  }

  export interface Booking {
    id: number;
    ticket: {
      id: number;
      event: string;
      seat: string;
      price: number;
    };
    user: string;
    bookingDate: string; // This will be a string in ISO format
  }