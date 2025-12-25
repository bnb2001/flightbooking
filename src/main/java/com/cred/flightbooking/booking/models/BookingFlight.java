package com.cred.flightbooking.booking.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_flights")
@Data
@NoArgsConstructor
public class BookingFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Booking booking;

    @Column(name = "flight_schedule_id", nullable = false)
    private Long flightScheduleId;

    @Column(name = "flight_schedule_seat_price_id", nullable = false)
    private Long flightScheduleSeatPriceId;

    public BookingFlight(Booking booking, Long flightScheduleId, Long flightScheduleSeatPriceId) {
        this.booking = booking;
        this.flightScheduleId = flightScheduleId;
        this.flightScheduleSeatPriceId = flightScheduleSeatPriceId;
    }
}
