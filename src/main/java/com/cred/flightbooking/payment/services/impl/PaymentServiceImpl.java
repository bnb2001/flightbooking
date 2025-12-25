package com.cred.flightbooking.payment.services.impl;

import com.cred.flightbooking.booking.enums.BookingStatus;
import com.cred.flightbooking.booking.models.Booking;
import com.cred.flightbooking.booking.repository.BookingRepository;
import com.cred.flightbooking.payment.models.Payment;
import com.cred.flightbooking.payment.repository.PaymentRepository;
import com.cred.flightbooking.payment.services.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@lombok.RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Payment processPayment(Long bookingId, Double amount) {
        // Stub: Always success
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentStatus("SUCCESS");
        payment.setPaymentTime(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Update booking status
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found for payment"));
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return savedPayment;
    }
}
