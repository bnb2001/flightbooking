package com.cred.flightbooking.booking.models;

import com.cred.flightbooking.booking.enums.BookingStatus;
import com.cred.flightbooking.search.enums.SeatType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We store the ID to keep modules loosely coupled, or could use JPA
    // relationship if we want tight coupling.
    // Given the requirement "separate three parts", loose coupling via ID might be
    // better,
    // but typically monolithic JPA uses object references.
    // I will use IDs for cross-module references to respect modularity,
    // but User is often shared. Let's use IDs for FlightSchedule.

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingFlight> bookingFlights = new ArrayList<>();

    public void addBookingFlight(BookingFlight bookingFlight) {
        bookingFlights.add(bookingFlight);
        bookingFlight.setBooking(this);
    }

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private Integer noSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Double totalAmount;

    private LocalDateTime bookingTime;
}
