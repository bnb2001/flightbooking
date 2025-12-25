package com.cred.flightbooking.payment.controller;

import com.cred.flightbooking.payment.models.Payment;
import com.cred.flightbooking.payment.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@lombok.RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(@RequestParam Long bookingId, @RequestParam Double amount) {
        return ResponseEntity.ok(paymentService.processPayment(bookingId, amount));
    }
}
