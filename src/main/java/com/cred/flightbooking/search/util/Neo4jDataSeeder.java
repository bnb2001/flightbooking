package com.cred.flightbooking.search.util;

import com.cred.flightbooking.search.models.Airport;
import com.cred.flightbooking.search.repository.GraphFlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class Neo4jDataSeeder implements CommandLineRunner {

        private final GraphFlightRepository graphFlightRepository;
        private final Neo4jClient neo4jClient;

        @Override
        public void run(String... args) throws Exception {
                // Clear existing data
                neo4jClient.query("MATCH (n) DETACH DELETE n").run();

                // Create Airports
                Airport del = new Airport();
                del.setCode("DEL");
                del.setName("Indira Gandhi International Airport");
                del.setCity("New Delhi");
                Airport bom = new Airport();
                bom.setCode("BOM");
                bom.setName("Chhatrapati Shivaji Maharaj International Airport");
                del.setCity("Mumbai");
                Airport blr = new Airport();
                blr.setCode("BLR");
                blr.setName("Kempegowda International Airport");
                blr.setCity("Bengaluru");
                Airport dxb = new Airport();
                dxb.setCode("DXB");
                dxb.setName("Dubai International Airport");
                dxb.setCity("Dubai");
                Airport lhr = new Airport();
                lhr.setCode("LHR");
                lhr.setName("Heathrow Airport");
                lhr.setCity("London");

                graphFlightRepository.saveAll(Arrays.asList(del, bom, blr, dxb, lhr));

                // Create Flights (Relationships) using Cypher for precise relationship
                // properties
                // DEL -> BOM (08:00 - 10:00)
                createFlight("DEL", "BOM", 1L, LocalDateTime.now().plusDays(1).withHour(8).withMinute(0),
                                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0), 5000.0);

                // BOM -> BLR (14:00 - 16:00) --> Valid connection from DEL (4 hours gap)
                createFlight("BOM", "BLR", 2L, LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                                LocalDateTime.now().plusDays(1).withHour(16).withMinute(0), 4000.0);

                // DEL -> BLR (Direct) (09:00 - 11:30)
                createFlight("DEL", "BLR", 3L, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                                LocalDateTime.now().plusDays(1).withHour(11).withMinute(30), 7000.0);

                // DEL -> DXB (10:00 - 14:00)
                createFlight("DEL", "DXB", 5L, LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                                LocalDateTime.now().plusDays(1).withHour(14).withMinute(0), 15000.0);

                // DXB -> LHR (18:00 - 22:00) --> Valid connection from DEL (4 hours gap)
                createFlight("DXB", "LHR", 6L, LocalDateTime.now().plusDays(1).withHour(18).withMinute(0),
                                LocalDateTime.now().plusDays(1).withHour(22).withMinute(0), 30000.0);

                // RETURN FLIGHTS for Round Trip Test
                // BOM -> DEL (18:00 - 20:00) on Day 2
                createFlight("BOM", "DEL", 4L, LocalDateTime.now().plusDays(2).withHour(18).withMinute(0),
                                LocalDateTime.now().plusDays(2).withHour(20).withMinute(0), 5500.0);

                log.info("Neo4j Data Seeded Successfully!");
        }

        private void createFlight(String from, String to, Long scheduleId, LocalDateTime dep, LocalDateTime arr,
                        Double price) {
                String query = "MATCH (a:Airport {code: $from}), (b:Airport {code: $to}) " +
                                "CREATE (a)-[:FLIES_TO {scheduleId: $scheduleId, departureTime: $dep, arrivalTime: $arr, price: $price}]->(b)";

                neo4jClient.query(query)
                                .bind(from).to("from")
                                .bind(to).to("to")
                                .bind(scheduleId).to("scheduleId")
                                .bind(dep).to("dep")
                                .bind(arr).to("arr")
                                .bind(price).to("price")
                                .run();
        }
}
