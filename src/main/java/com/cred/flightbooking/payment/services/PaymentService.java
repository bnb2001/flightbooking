package com.cred.flightbooking.payment.services;

import com.cred.flightbooking.payment.models.Payment;

public interface PaymentService {
    Payment processPayment(Long bookingId, Double amount);
}
