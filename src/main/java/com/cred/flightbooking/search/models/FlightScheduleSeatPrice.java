package com.cred.flightbooking.search.models;

import com.cred.flightbooking.search.enums.SeatStatus;
import com.cred.flightbooking.search.enums.SeatType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "flight_schedule_seat_prices")
public class FlightScheduleSeatPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_schedule_id", nullable = false)
    private FlightSchedule flightSchedule;

    private String seatNo;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Double seatPrice;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;
}
