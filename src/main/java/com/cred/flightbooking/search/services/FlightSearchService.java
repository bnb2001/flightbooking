package com.cred.flightbooking.search.services;

import com.cred.flightbooking.search.models.FlightSchedule;
import java.time.LocalDate;
import java.util.List;

public interface FlightSearchService {
    List<FlightSchedule> searchFlights(String from, String to, LocalDate dateOfJourney, int numberOfSeats,
            String flightClass);
}
