package com.cred.flightbooking.search.repository;

import com.cred.flightbooking.search.models.Airport;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface GraphFlightRepository extends Neo4jRepository<Airport, String> {

    // Find paths with relationship properties
    // We return list of maps because we need properties of relationships in the
    // path

    // Direct Flights
    @Query("MATCH (source:Airport {code: $from})-[r:FLIES_TO]->(target:Airport {code: $to}) " +
            "WHERE r.departureTime >= $departureTime " +
            "RETURN source, r, target")
    List<Airport> findDirectFlights(String from, String to, LocalDateTime departureTime);

    // Connecting Flights (1 stop)
    // Return paths: Source -> Leg1 -> Mid -> Leg2 -> Target
    // Constraint: Leg2.departureTime > Leg1.arrivalTime + buffer (e.g. 1 hour)
    @Query("MATCH (source:Airport {code: $from})-[r1:FLIES_TO]->(mid:Airport)-[r2:FLIES_TO]->(target:Airport {code: $to}) "
            +
            "WHERE r1.departureTime >= $departureTime " +
            "AND r2.departureTime > r1.arrivalTime " +
            "RETURN source, r1, mid, r2, target")
    List<Airport> findConnectingFlights(String from, String to, LocalDateTime departureTime);
}
