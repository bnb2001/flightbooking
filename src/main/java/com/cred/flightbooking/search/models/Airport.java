package com.cred.flightbooking.search.models;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Airport")
@Data
public class Airport {
    @Id
    private String code; // IATA code, e.g., DEL, BOM

    private String name;
    private String city;
}
