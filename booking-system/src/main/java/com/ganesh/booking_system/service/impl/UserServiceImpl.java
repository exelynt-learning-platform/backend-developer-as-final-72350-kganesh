package com.ganesh.booking_system.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ganesh.booking_system.dto.RegisterRequest;
import com.ganesh.booking_system.entity.User;
import com.ganesh.booking_system.enums.Role;
import com.ganesh.booking_system.repository.UserRepository;
import com.ganesh.booking_system.service.UserService;
import com.ganesh.booking_system.exception.BadRequestException;

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

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(
                    "Username already exists"
            );
        }

        Role role;

        try {
            role = Role.valueOf(
                    request.getRole().toUpperCase()
            );
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid role: " + request.getRole()
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUsername(),
                encodedPassword,
                role
        );

        return userRepository.save(user);
    }
}