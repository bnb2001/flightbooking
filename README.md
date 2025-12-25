# Flight Booking System (Microservices Prototype)

A comprehensive Flight Booking System backend built with Spring Boot, Neo4j, MySQL, and Redis. It features graph-based flight search (supporting connecting flights and round trips), transactional booking with distributed locking, and a detailed seeder for testing.

## Features

-   **User Management**: Register and retrieve users (MySQL).
-   **Advanced Flight Search**:
    -   **Graph-based Search (Neo4j)**: Finds direct and connecting flights (A -> B -> C).
    -   **Round Trip Support**: Returns outbound and inward journey options.
    -   **Seat Availability**: Filters flights based on real-time seat inventory in MySQL.
-   **Booking System**:
    -   **Multi-Leg Booking**: Atomic transactions for booking multiple flight segments.
    -   **Distributed Locking (Redis)**: Prevents race conditions and double-booking.
    -   **Inventory Management**: Updates booked seat counts in MySQL.
-   **Payment Stub**: API to simulate payment processing.

## Tech Stack

-   **Java 17**
-   **Spring Boot 3**
-   **Neo4j** (Graph Database for flight routes)
-   **MySQL 8** (Relational Database for bookings, availability, users)
-   **Redis** (Distributed Locking)
-   **Docker & Docker Compose** (Containerization)

## Prerequisites

-   Docker & Docker Compose
-   Java 17 (for local development w/o Docker)
-   Maven (for local development w/o Docker)

## Getting Started

### 1. Docker Setup (Recommended)

Run the entire stack (App, MySQL, Neo4j, Redis) with a single command:

```bash
docker-compose up --build
```

The application will start on port `8080`.
-   **API Base URL**: `http://localhost:8080`
-   **Neo4j Browser**: `http://localhost:7474` (Auth: `neo4j` / `password`)

### 2. Manual Setup (Local Dev)

1.  **Start Infrastructure**:
    ```bash
    docker-compose up neo4j redis mysql -d
    ```
2.  **Run Application**:
    ```bash
    ./mvnw spring-boot:run
    ```

## API Documentation

See [api_documentation.md](api_documentation.md) for detailed API usage, request schemas, and curl examples.

## Testing the Application

### Seed Data
On startup, the application creates a test user and flight schedules (DEL <-> BOM <-> BLR, DEL <-> DXB <-> LHR).

### Quick Verification Steps

1.  **Search Flights (One Way)**:
    ```bash
    curl "http://localhost:8080/api/flights/graph-search?from=DEL&to=BOM&dateOfJourney=2025-12-26&seats=1"
    ```

2.  **Create Booking (Multi-Leg)**:
    ```bash
    curl -X POST http://localhost:8080/api/bookings \
    -H "Content-Type: application/json" \
    -d '{
      "flightScheduleIds": [1, 2],
      "flightScheduleSeatPriceIds": [1, 61],
      "userId": 1,
      "noSeats": 1,
      "seatType": "ECONOMY"
    }'
    ```

## Project Structure

-   `src/main/java/com/cred/flightbooking`:
    -   `booking`: Booking logic, distributed locking, repository.
    -   `search`: Neo4j integration, graph search service, seeders.
    -   `common`: Shared entities (User), global configs.
    -   `payment`: Payment stub controller.
