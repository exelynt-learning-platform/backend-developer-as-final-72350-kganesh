package com.ganesh.booking_system.service;

import com.ganesh.booking_system.dto.ReservationRequest;
import com.ganesh.booking_system.dto.ReservationResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;    
import java.util.List;

public interface ReservationService {

    // USER - Create reservation
    ReservationResponse createReservation(ReservationRequest request);

    // USER - Get only own reservations
    List<ReservationResponse> getMyReservations();

    // USER + ADMIN - Get reservation by ID
    ReservationResponse getReservationById(Long id);

    // ADMIN - Get all reservations with filtering,
    // pagination and sorting
    Page<ReservationResponse> getAllReservations(
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    // ADMIN - Update reservation status
    ReservationResponse updateReservationStatus(Long id,String status);

    // USER - Cancel own reservation
    void cancelReservation(Long id);
}
