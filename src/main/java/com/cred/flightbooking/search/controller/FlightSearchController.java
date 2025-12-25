package com.cred.flightbooking.search.controller;

import com.cred.flightbooking.search.models.FlightSchedule;
import com.cred.flightbooking.search.services.FlightSearchService;
import com.cred.flightbooking.search.services.GraphFlightSearchService;
import com.cred.flightbooking.search.dtos.FlightResponse;
import com.cred.flightbooking.search.enums.JourneyType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@lombok.RequiredArgsConstructor
public class FlightSearchController {

    private final FlightSearchService flightSearchService;
    private final GraphFlightSearchService graphFlightSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<FlightSchedule>> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfJourney,
            @RequestParam(defaultValue = "1") int seats,
            @RequestParam(defaultValue = "ECONOMY") String flightClass) {

        List<FlightSchedule> flights = flightSearchService.searchFlights(from, to, dateOfJourney, seats, flightClass);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/graph-search")
    public ResponseEntity<FlightResponse> searchGraphFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfJourney,
            @RequestParam(defaultValue = "ONE_WAY") JourneyType journeyType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(defaultValue = "1") int seats) {

        return ResponseEntity
                .ok(graphFlightSearchService.searchFlights(from, to, dateOfJourney, journeyType, returnDate, seats));
    }
}
