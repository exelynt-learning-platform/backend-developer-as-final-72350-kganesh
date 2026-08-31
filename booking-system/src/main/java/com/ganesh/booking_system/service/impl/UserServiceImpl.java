package com.ganesh.booking_system.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ganesh.booking_system.dto.RegisterRequest;
import com.ganesh.booking_system.entity.User;
import com.ganesh.booking_system.enums.Role;
import com.ganesh.booking_system.repository.UserRepository;
import com.ganesh.booking_system.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User registerUser(RegisterRequest request) {

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Role role = Role.valueOf(
                request.getRole().toUpperCase()
        );

        User user = new User(
                request.getUsername(),
                encodedPassword,
                role
        );

        return userRepository.save(user);
    }
}