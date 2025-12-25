package com.cred.flightbooking.search.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {
    private List<FlightOption> outbound;
    private List<FlightOption> inward;
}
