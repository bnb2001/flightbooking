package com.cred.flightbooking.search.services.impl;

import com.cred.flightbooking.search.dtos.FlightOption;
import com.cred.flightbooking.search.dtos.FlightResponse;
import com.cred.flightbooking.search.dtos.FlightSegment;
import com.cred.flightbooking.search.enums.JourneyType;
import com.cred.flightbooking.search.enums.SeatType;
import com.cred.flightbooking.search.models.FlightScheduleSeat;
import com.cred.flightbooking.search.repository.FlightScheduleSeatRepository;
import com.cred.flightbooking.search.services.GraphFlightSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraphFlightSearchServiceImpl implements GraphFlightSearchService {

        private final Neo4jClient neo4jClient;
        private final FlightScheduleSeatRepository flightScheduleSeatRepository;

        @Override
        public FlightResponse searchFlights(String from, String to, LocalDate dateOfJourney, JourneyType journeyType,
                        LocalDate returnDate, int seats) {

                List<FlightOption> outbound = searchOneWay(from, to, dateOfJourney, seats);
                List<FlightOption> inward = null;

                if (JourneyType.ROUND_TRIP == journeyType && returnDate != null) {
                        inward = searchOneWay(to, from, returnDate, seats);
                }

                return new FlightResponse(outbound, inward);
        }

        private List<FlightOption> searchOneWay(String from, String to, LocalDate dateOfJourney, int seats) {
                String query = "MATCH paths = (source:Airport {code: $from})-[flight:FLIES_TO*1..2]->(destination:Airport {code: $to}) "
                                +
                                "WHERE all(r IN relationships(paths) WHERE r.departureTime >= $startOfDay AND r.departureTime < $endOfDay) "
                                +
                                "WITH paths, relationships(paths) AS flights " +
                                "WHERE size(flights) = 1 " + // Direct flights
                                "OR (size(flights) = 2 AND " +
                                "duration.inSeconds(flights[0].arrivalTime, flights[1].departureTime).seconds >= 7200) "
                                + // > 2 hours layover
                                "RETURN { " +
                                "type: CASE WHEN size(flights) = 1 THEN 'DIRECT' ELSE 'CONNECTING' END, " +
                                "stop: CASE WHEN size(flights) = 2 THEN nodes(paths)[1].code ELSE null END, " +
                                "flights: [f IN flights | { " +
                                "scheduleId: f.scheduleId, " +
                                "from: startNode(f).code, " +
                                "to: endNode(f).code, " +
                                "departureTime: f.departureTime, " +
                                "arrivalTime: f.arrivalTime, " +
                                "price: f.price " +
                                "}] " +
                                "} AS option " +
                                "ORDER BY reduce(s = 0.0, f IN flights | s + f.price) ASC";

                LocalDateTime startOfDay = dateOfJourney.atStartOfDay();
                LocalDateTime endOfDay = dateOfJourney.plusDays(1).atStartOfDay();

                Collection<Map<String, Object>> result = neo4jClient.query(query)
                                .bind(from).to("from")
                                .bind(to).to("to")
                                .bind(startOfDay).to("startOfDay")
                                .bind(endOfDay).to("endOfDay")
                                .fetch()
                                .all();

                List<FlightOption> options = mapToFlightOptions(new ArrayList<>(result));
                return filterByAvailability(options, seats);
        }

        private List<FlightOption> filterByAvailability(List<FlightOption> options, int requiredSeats) {
                return options.stream()
                                .filter(option -> isOptionAvailable(option, requiredSeats))
                                .collect(Collectors.toList());
        }

        private boolean isOptionAvailable(FlightOption option, int requiredSeats) {
                for (FlightSegment segment : option.getFlights()) {
                        boolean segmentAvailable = checkSegmentAvailability(segment.getScheduleId(), requiredSeats);
                        if (!segmentAvailable) {
                                return false;
                        }
                }
                return true;
        }

        private boolean checkSegmentAvailability(Long scheduleId, int requiredSeats) {
                Optional<FlightScheduleSeat> scheduleSeat = flightScheduleSeatRepository
                                .findByFlightScheduleIdAndSeatType(scheduleId, SeatType.ECONOMY);

                // If seat record found, check availability. If not found, assume unavailable
                // (or seeded data missing)
                return scheduleSeat.map(seat -> seat.getAvailableSeats() >= requiredSeats).orElse(false);
        }

        private List<FlightOption> mapToFlightOptions(Collection<Map<String, Object>> results) {
                return results.stream().map(result -> {
                        Map<String, Object> optionMap = (Map<String, Object>) result.get("option");
                        String type = (String) optionMap.get("type");
                        String stop = (String) optionMap.get("stop");

                        List<Map<String, Object>> flightsList = (List<Map<String, Object>>) optionMap.get("flights");
                        List<FlightSegment> segments = flightsList.stream().map(f -> new FlightSegment(
                                        (Long) f.get("scheduleId"),
                                        (String) f.get("from"),
                                        (String) f.get("to"),
                                        (LocalDateTime) f.get("departureTime"),
                                        (LocalDateTime) f.get("arrivalTime"),
                                        (Double) f.get("price"))).toList();

                        Double totalPrice = segments.stream().mapToDouble(FlightSegment::getPrice).sum();

                        return new FlightOption(type, segments, totalPrice, stop);
                }).toList();
        }
}
