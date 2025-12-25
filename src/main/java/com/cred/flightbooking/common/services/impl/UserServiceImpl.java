package com.cred.flightbooking.common.services.impl;

import com.cred.flightbooking.common.models.User;
import com.cred.flightbooking.common.repository.UserRepository;
import com.cred.flightbooking.common.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@lombok.RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User registerUser(User user) {
        // Basic validation or password encryption would go here
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
}
