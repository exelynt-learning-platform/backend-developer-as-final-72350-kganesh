package com.ganesh.booking_system.service;

import java.util.Optional;

import com.ganesh.booking_system.dto.RegisterRequest;
import com.ganesh.booking_system.entity.User;

public interface UserService {

    Optional<User> findByUsername(String username);
    
    User registerUser(RegisterRequest request);
}

