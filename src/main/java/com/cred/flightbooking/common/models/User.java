package com.cred.flightbooking.common.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password; // In real app, this should be encrypted
    private String phoneNumber;

    // Additional fields as needed
}
