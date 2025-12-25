package com.cred.flightbooking.search.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSegment {
    private Long scheduleId;
    private String from;
    private String to;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Double price;
}
