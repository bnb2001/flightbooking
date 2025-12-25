package com.cred.flightbooking.search.models;

import com.cred.flightbooking.search.enums.SeatType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "flight_schedule_seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "flight_schedule_id", "seatType" })
})
public class FlightScheduleSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_schedule_id", nullable = false)
    private FlightSchedule flightSchedule;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "booked_seats", nullable = false)
    private Integer bookedSeats;

    // Helper method for available seats
    public Integer getAvailableSeats() {
        return totalSeats - bookedSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        // This is a derived setter, logic to adjust bookedSeats or throw error
        // For simplicity: booked_seats = total - available
        this.bookedSeats = this.totalSeats - availableSeats;
    }
}
