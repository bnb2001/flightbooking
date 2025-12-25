package com.cred.flightbooking.search.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class MySQLDataSeeder implements CommandLineRunner {

        private final JdbcTemplate jdbcTemplate;

        @Override
        public void run(String... args) throws Exception {
                // Disable FK checks to allow truncation
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

                // Truncate tables to ensure clean state matching Neo4j
                jdbcTemplate.execute("TRUNCATE TABLE flight_schedule_seat_prices");
                jdbcTemplate.execute("TRUNCATE TABLE flight_schedule_seats");
                jdbcTemplate.execute("TRUNCATE TABLE flight_schedules");
                jdbcTemplate.execute("TRUNCATE TABLE flights");

                // 1. Insert Flights
                // Flight Air India AI-101
                jdbcTemplate.update(
                                "INSERT INTO flights (id, airline, flight_airline_id, flight_type) VALUES (?, ?, ?, ?)",
                                1L, "Air India", "AI-101", "BOEING");
                // Flight Emirates EK-201
                jdbcTemplate.update(
                                "INSERT INTO flights (id, airline, flight_airline_id, flight_type) VALUES (?, ?, ?, ?)",
                                2L, "Emirates", "EK-201", "BOEING");

                // 2. Insert Flight Schedules (Matching Neo4j IDs: 101, 102, 103, 104, 201, 202)
                // SCH_101: DEL -> BOM
                insertSchedule(1L, 1L, "DEL", "BOM", "2025-12-26 08:00:00", "2025-12-26 10:00:00", 5000.0);
                // SCH_102: BOM -> BLR
                insertSchedule(2L, 1L, "BOM", "BLR", "2025-12-26 14:00:00", "2025-12-26 16:00:00", 4000.0);
                // SCH_103: DEL -> BLR (Direct)
                insertSchedule(3L, 1L, "DEL", "BLR", "2025-12-26 09:00:00", "2025-12-26 11:30:00", 7000.0);
                // SCH_104: BOM -> DEL (Return)
                insertSchedule(4L, 1L, "BOM", "DEL", "2025-12-27 18:00:00", "2025-12-27 20:00:00", 5500.0);

                // SCH_201: DEL -> DXB
                insertSchedule(5L, 2L, "DEL", "DXB", "2025-12-26 10:00:00", "2025-12-26 14:00:00", 15000.0);
                // SCH_202: DXB -> LHR
                insertSchedule(6L, 2L, "DXB", "LHR", "2025-12-26 18:00:00", "2025-12-26 22:00:00", 30000.0);

                // 3. Insert Seats & Prices
                // For simplicity, giving 60 seats to all flights, ECONOMY only
                insertSeatsAndPrice(1L, 60, 5000.0);
                insertSeatsAndPrice(2L, 40, 4000.0);
                insertSeatsAndPrice(3L, 60, 7000.0);
                insertSeatsAndPrice(4L, 60, 5500.0);
                insertSeatsAndPrice(5L, 100, 15000.0);
                insertSeatsAndPrice(6L, 100, 30000.0);

                // 4. Insert Test User
                jdbcTemplate.execute("TRUNCATE TABLE users");
                // Check if users table has correct columns based on User entity.
                // User entity: id, username, email, password, phoneNumber
                jdbcTemplate.update(
                                "INSERT INTO users (id, username, email, password, phone_number) VALUES (?, ?, ?, ?, ?)",
                                1L, "booking_test_user", "booking_test@example.com", "password", "1234567890");

                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
                log.info("MySQL Data Seeded Successfully with IDs matching Neo4j!");
        }

        private void insertSchedule(Long id, Long flightId, String from, String to, String start, String end,
                        Double price) {
                jdbcTemplate.update(
                                "INSERT INTO flight_schedules (id, flight_id, `from`, `to`, start_time, end_time, price) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                id, flightId, from, to, start, end, price);
        }

        private void insertSeatsAndPrice(Long scheduleId, int totalSeats, Double price) {
                // Insert FlightScheduleSeat
                jdbcTemplate.update(
                                "INSERT INTO flight_schedule_seats (flight_schedule_id, seat_type, total_seats, booked_seats) VALUES (?, ?, ?, ?)",
                                scheduleId, "ECONOMY", totalSeats, 0);

                // Insert FlightScheduleSeatPrice: One entry per SEAT
                for (int i = 1; i <= totalSeats; i++) {
                        String seatNo = "Seat-" + i;
                        jdbcTemplate.update(
                                        "INSERT INTO flight_schedule_seat_prices (flight_schedule_id, seat_type, seat_price, seat_status, seat_no) VALUES (?, ?, ?, ?, ?)",
                                        scheduleId, "ECONOMY", price, "AVAILABLE", seatNo);
                }
        }
}
