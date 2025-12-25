package com.cred.flightbooking.search.repository;

import com.cred.flightbooking.search.models.FlightScheduleSeatPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightScheduleSeatPriceRepository extends JpaRepository<FlightScheduleSeatPrice, Long> {
}
