package com.cred.flightbooking.booking.services.impl;

import com.cred.flightbooking.booking.models.Booking;
import com.cred.flightbooking.booking.models.BookingFlight;
import com.cred.flightbooking.booking.models.BookingRequest;
import com.cred.flightbooking.booking.repository.BookingRepository;
import com.cred.flightbooking.booking.services.BookingService;
import com.cred.flightbooking.booking.services.RedisLockService;
import com.cred.flightbooking.search.models.FlightScheduleSeatPrice;
import com.cred.flightbooking.search.enums.SeatStatus;
import com.cred.flightbooking.search.repository.FlightScheduleSeatPriceRepository;
import com.cred.flightbooking.search.models.FlightScheduleSeat;
import com.cred.flightbooking.search.repository.FlightScheduleSeatRepository;
import com.cred.flightbooking.common.models.User;
import com.cred.flightbooking.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightScheduleSeatPriceRepository flightScheduleSeatPriceRepository;
    private final FlightScheduleSeatRepository flightScheduleSeatRepository;
    private final RedisLockService redisLockService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking createBooking(BookingRequest request) {
        validateRequest(request);

        List<String> lockKeys = generateLockKeys(request.getFlightScheduleSeatPriceIds());
        acquireLocks(lockKeys);

        try {
            List<FlightScheduleSeatPrice> seatPrices = processSeatPrices(request.getFlightScheduleSeatPriceIds());
            updateSeatCounts(seatPrices); // Update booked seats count

            Double totalAmount = calculateTotalAmount(seatPrices);

            Booking booking = initializeBooking(request, totalAmount);
            Booking savedBooking = bookingRepository.save(booking);

            saveBookingFlights(savedBooking, request.getFlightScheduleIds(), request.getFlightScheduleSeatPriceIds());

            return savedBooking;
        } finally {
            redisLockService.releaseLocks(lockKeys);
        }
    }

    private void validateRequest(BookingRequest request) {
        if (request.getFlightScheduleSeatPriceIds() == null || request.getFlightScheduleSeatPriceIds().isEmpty()) {
            throw new IllegalArgumentException("Seat Price IDs are required");
        }
    }

    private List<String> generateLockKeys(List<Long> ids) {
        return ids.stream().map(id -> "LOCK_SEAT_PRICE_" + id).toList();
    }

    private void acquireLocks(List<String> lockKeys) {
        if (!redisLockService.acquireLocks(lockKeys)) {
            throw new RuntimeException("Could not acquire locks for seats. Please try again.");
        }
    }

    private List<FlightScheduleSeatPrice> processSeatPrices(List<Long> ids) {
        List<FlightScheduleSeatPrice> prices = flightScheduleSeatPriceRepository.findAllById(ids);
        if (prices.size() != ids.size()) {
            throw new RuntimeException("Some seat prices were not found");
        }

        prices.forEach(price -> {
            if (SeatStatus.BOOKED.equals(price.getSeatStatus())) {
                throw new RuntimeException("Seat " + price.getSeatNo() + " is already booked.");
            }
            price.setSeatStatus(SeatStatus.BOOKED);
        });

        return flightScheduleSeatPriceRepository.saveAll(prices);
    }

    private void updateSeatCounts(List<FlightScheduleSeatPrice> prices) {
        for (FlightScheduleSeatPrice price : prices) {
            FlightScheduleSeat seatInfo = flightScheduleSeatRepository
                    .findByFlightScheduleIdAndSeatType(price.getFlightSchedule().getId(), price.getSeatType())
                    .orElseThrow(() -> new RuntimeException(
                            "Seat info not found for schedule " + price.getFlightSchedule().getId()));

            if (seatInfo.getAvailableSeats() <= 0) {
                throw new RuntimeException("No seats available for type " + price.getSeatType());
            }

            seatInfo.setBookedSeats(seatInfo.getBookedSeats() + 1);
            flightScheduleSeatRepository.save(seatInfo);
        }
    }

    private Double calculateTotalAmount(List<FlightScheduleSeatPrice> prices) {
        // Total = Base Price (from Schedule) + Seat Price
        return prices.stream()
                .mapToDouble(price -> {
                    Double basePrice = price.getFlightSchedule().getPrice();
                    if (basePrice == null)
                        basePrice = 0.0;
                    return basePrice + price.getSeatPrice();
                })
                .sum();
    }

    private Booking initializeBooking(BookingRequest request, Double totalAmount) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setNoSeats(request.getNoSeats());
        booking.setSeatType(request.getSeatType());
        booking.setStatus(com.cred.flightbooking.booking.enums.BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalAmount(totalAmount);
        return booking;
    }

    private void saveBookingFlights(Booking booking, List<Long> scheduleIds, List<Long> seatPriceIds) {
        for (int i = 0; i < scheduleIds.size(); i++) {
            BookingFlight bookingFlight = new BookingFlight(booking, scheduleIds.get(i), seatPriceIds.get(i));
            booking.addBookingFlight(bookingFlight);
        }
    }

    @Override
    public Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
}
