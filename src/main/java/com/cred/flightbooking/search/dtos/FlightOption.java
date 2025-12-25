package com.cred.flightbooking.search.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightOption {
    private String type; // DIRECT or CONNECTING
    private List<FlightSegment> flights;
    private Double price;
    private String stop; // Airport code if connecting, else null
}
