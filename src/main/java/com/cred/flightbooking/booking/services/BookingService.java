package com.cred.flightbooking.booking.services;

import com.cred.flightbooking.booking.models.Booking;
import com.cred.flightbooking.search.enums.SeatType;

public interface BookingService {
    Booking createBooking(com.cred.flightbooking.booking.models.BookingRequest bookingRequest);

    Booking getBooking(Long bookingId);

    java.util.List<Booking> getBookingsByUser(Long userId);
}
