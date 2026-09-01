package com.ganesh.booking_system.service;

import com.ganesh.booking_system.dto.ReservationRequest;
import com.ganesh.booking_system.dto.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);

    List<ReservationResponse> getMyReservations();

    ReservationResponse getReservationById(Long id);

    List<ReservationResponse> getAllReservations();

    ReservationResponse updateReservationStatus(Long id,String status);

    void cancelReservation(Long id);
}
