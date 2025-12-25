package com.cred.flightbooking.search.models;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
@Data
public class FlightRelationship {

    @RelationshipId
    private Long id;

    private String scheduleId; // Reference to MySQL FlightSchedule ID

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Double price;

    @TargetNode
    private Airport targetAirport;
}
