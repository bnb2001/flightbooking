package com.cred.flightbooking.common.services;

import com.cred.flightbooking.common.models.User;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);
}
