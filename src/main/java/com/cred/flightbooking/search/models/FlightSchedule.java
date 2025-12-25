package com.cred.flightbooking.search.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "flight_schedules")
public class FlightSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "`from`") // 'from' is a reserved keyword in SQL
    private String from;

    @Column(name = "`to`") // 'to' is a reserved keyword in SQL
    private String to;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Base price or starting price could also be here if needed,
    // but schema says price is in FlightSchedule (USER_REQUEST point 3)
    private Double price;
}
