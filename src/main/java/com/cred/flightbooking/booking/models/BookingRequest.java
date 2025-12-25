package com.cred.flightbooking.booking.models;

import com.cred.flightbooking.search.enums.SeatType;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    private List<Long> flightScheduleIds;
    private List<Long> flightScheduleSeatPriceIds;
    private Long userId;
    private Integer noSeats;
    private SeatType seatType;
}
