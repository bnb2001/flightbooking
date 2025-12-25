package com.cred.flightbooking.search.services.impl;

import com.cred.flightbooking.search.models.FlightSchedule;
import com.cred.flightbooking.search.repository.FlightScheduleRepository;
import com.cred.flightbooking.search.services.FlightSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightSearchServiceImpl implements FlightSearchService {

    private final FlightScheduleRepository flightScheduleRepository;

    @Override
    public List<FlightSchedule> searchFlights(String from, String to, LocalDate dateOfJourney, int numberOfSeats,
            String flightClass) {
        LocalDateTime startOfDay = dateOfJourney.atStartOfDay();
        LocalDateTime endOfDay = dateOfJourney.atTime(23, 59, 59);

        // Note: For production, we would also filter by seat availability
        // (numberOfSeats) and class (flightClass) here or in query.
        // For prototype, we return schedules matching route and date.
        return flightScheduleRepository.searchFlights(from, to, startOfDay, endOfDay);
    }
}
