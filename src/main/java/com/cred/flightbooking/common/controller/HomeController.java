package com.cred.flightbooking.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Flight Booking Application is running!";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "UP";
    }
}
