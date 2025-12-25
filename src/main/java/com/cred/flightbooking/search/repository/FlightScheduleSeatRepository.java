package com.cred.flightbooking.search.repository;

import com.cred.flightbooking.search.models.FlightScheduleSeat;
import com.cred.flightbooking.search.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightScheduleSeatRepository extends JpaRepository<FlightScheduleSeat, Long> {
    Optional<FlightScheduleSeat> findByFlightScheduleIdAndSeatType(Long flightScheduleId, SeatType seatType);
}
