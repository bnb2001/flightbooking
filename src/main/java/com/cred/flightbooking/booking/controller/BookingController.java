package com.cred.flightbooking.booking.controller;

import com.cred.flightbooking.booking.models.Booking;
import com.cred.flightbooking.booking.services.BookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@lombok.RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody com.cred.flightbooking.booking.models.BookingRequest bookingRequest) {
        try {
            Booking booking = bookingService.createBooking(bookingRequest);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            log.error("Booking failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build(); // Simplify error handling for prototype
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<java.util.List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }
}
