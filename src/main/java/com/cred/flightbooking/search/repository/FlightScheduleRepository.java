package com.cred.flightbooking.search.repository;

import com.cred.flightbooking.search.models.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {

    @Query("SELECT fs FROM FlightSchedule fs WHERE fs.from = :from AND fs.to = :to AND fs.startTime BETWEEN :startOfDay AND :endOfDay")
    List<FlightSchedule> searchFlights(@Param("from") String from, @Param("to") String to,
            @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
