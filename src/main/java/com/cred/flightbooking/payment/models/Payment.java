package com.cred.flightbooking.payment.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    private Double amount;

    private String paymentStatus; // e.g., SUCCESS, FAILED

    private LocalDateTime paymentTime;
}
