package com.cred.flightbooking.search.services;

import java.time.LocalDate;
import java.util.List;
import com.cred.flightbooking.search.dtos.FlightResponse;
import com.cred.flightbooking.search.enums.JourneyType;

public interface GraphFlightSearchService {
    FlightResponse searchFlights(String from, String to, LocalDate dateOfJourney, JourneyType journeyType,
            LocalDate returnDate, int seats);
}
