package com.cred.flightbooking.search.models;

import com.cred.flightbooking.search.enums.FlightType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String airline;

    @Column(name = "flight_airline_id")
    private String flightAirlineId; // e.g., AI-202

    @Enumerated(EnumType.STRING)
    private FlightType flightType; // BOEING, etc.
}
