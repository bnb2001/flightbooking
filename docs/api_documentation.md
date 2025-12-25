# Flight Booking System - API Documentation

This document provides a comprehensive guide to the REST APIs available in the Flight Booking System.

## Base URL
`http://localhost:8080`

---

## 1. User Management

### 1.1 Register User
Register a new user in the system.

- **Endpoint**: `POST /api/users/register`
- **Content-Type**: `application/json`

**Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "1234567890"
}
```

**Response (200 OK)**:
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "1234567890"
}
```

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/users/register \
-H "Content-Type: application/json" \
-d '{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "1234567890"
}'
```

### 1.2 Get User
Retrieve user details by ID.

- **Endpoint**: `GET /api/users/{id}`

**Response (200 OK)**:
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "1234567890"
}
```

**Curl Command**:
```bash
curl http://localhost:8080/api/users/1
```

---

## 2. Flight Search

### 2.1 Graph Search (Advanced)
Search for flights using Neo4j graph optimized logic. Supports Direct, Connecting, Round Trip, and Seat Availability checks.

- **Endpoint**: `GET /api/flights/graph-search`

**Query Parameters**:
- `from`: Origin Airport Code (e.g., DEL)
- `to`: Destination Airport Code (e.g., BLR)
- `dateOfJourney`: Date in YYYY-MM-DD format
- `journeyType` (Optional): `ONE_WAY` (default) or `ROUND_TRIP`
- `returnDate` (Optional): Return date in YYYY-MM-DD format (required for Round Trip)
- `seats` (Optional): Number of seats required (default: 1)

**Response (200 OK - One Way)**:
```json
{
  "outbound": [
    {
      "type": "DIRECT",
      "flights": [
        {
          "scheduleId": 3,
          "from": "DEL",
          "to": "BLR",
          "departureTime": "2025-12-26T09:00:00",
          "arrivalTime": "2025-12-26T11:30:00",
          "price": 7000.0
        }
      ],
      "price": 7000.0,
      "stop": null
    }
  ],
  "inward": null
}
```

**Curl Command (One Way)**:
```bash
curl "http://localhost:8080/api/flights/graph-search?from=DEL&to=BLR&dateOfJourney=2025-12-26&seats=2"
```

**Curl Command (Round Trip)**:
```bash
curl "http://localhost:8080/api/flights/graph-search?from=DEL&to=BOM&dateOfJourney=2025-12-26&journeyType=ROUND_TRIP&returnDate=2025-12-27"
```

### 2.2 Basic Search (Legacy MySQL)
Simple search for direct flights stored in MySQL.

- **Endpoint**: `GET /api/flights/search`
- **Query Parameters**: `from`, `to`, `dateOfJourney`, `seats`, `flightClass`

**Curl Command**:
```bash
curl "http://localhost:8080/api/flights/search?from=DEL&to=BOM&dateOfJourney=2025-12-26"
```

---

## 3. Booking

### 3.1 Create Booking
Create a new booking. Supports multi-leg journeys and transactional integrity.

- **Endpoint**: `POST /api/bookings`
- **Content-Type**: `application/json`

**Request Body**:
```json
{
  "flightScheduleIds": [1, 2],
  "flightScheduleSeatPriceIds": [1, 61],
  "userId": 1,
  "noSeats": 1,
  "seatType": "ECONOMY"
}
```

**Response (200 OK)**:
```json
{
  "id": 1,
  "bookingFlights": [
    { "id": 1, "flightScheduleId": 1, "flightScheduleSeatPriceId": 1 },
    { "id": 2, "flightScheduleId": 2, "flightScheduleSeatPriceId": 61 }
  ],
  "userId": 1,
  "noSeats": 1,
  "status": "PENDING",
  "seatType": "ECONOMY",
  "totalAmount": 9000.0,
  "bookingTime": "2025-12-25T15:30:00"
}
```

**Curl Command**:
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

### 3.2 Get Booking by ID
Retrieve details of a specific booking.

- **Endpoint**: `GET /api/bookings/{id}`

**Curl Command**:
```bash
curl http://localhost:8080/api/bookings/1
```

### 3.3 Get Bookings by User
Retrieve all bookings made by a user.

- **Endpoint**: `GET /api/bookings/user/{userId}`

**Curl Command**:
```bash
curl http://localhost:8080/api/bookings/user/1
```

---

## 4. Payment

### 4.1 Process Payment
Process a dummy payment for a booking.

- **Endpoint**: `POST /api/payments/process`
- **Query Parameters**:
    - `bookingId`: ID of the booking
    - `amount`: Amount to pay

**Response (200 OK)**:
```json
{
  "id": 1,
  "bookingId": 1,
  "amount": 9000.0,
  "status": "SUCCESS",
  "paymentTime": "2025-12-25T15:35:00"
}
```

**Curl Command**:
```bash
curl -X POST "http://localhost:8080/api/payments/process?bookingId=1&amount=9000.0"
```

---

## 5. System Health

### 5.1 Health Check
Check if the application is running.

- **Endpoint**: `GET /health`
- **Response**: `UP`

**Curl Command**:
```bash
curl http://localhost:8080/health
```

### 5.2 Home
Welcome message.

- **Endpoint**: `GET /`
- **Response**: `Flight Booking Application is running!`

**Curl Command**:
```bash
curl http://localhost:8080/
```
