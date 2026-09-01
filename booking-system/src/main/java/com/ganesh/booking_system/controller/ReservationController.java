package com.ganesh.booking_system.controller;

import com.ganesh.booking_system.dto.ReservationRequest;
import com.ganesh.booking_system.dto.ReservationResponse;
import com.ganesh.booking_system.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService = reservationService;
    }


    // USER only
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        ReservationResponse response =
                reservationService.createReservation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // USER only - view own reservations
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationResponse>>
    getMyReservations() {

        return ResponseEntity.ok(
                reservationService.getMyReservations()
        );
    }


    // USER + ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservationResponse>
    getReservationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservationService.getReservationById(id)
        );
    }


    // ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationResponse>>
    getAllReservations() {

        return ResponseEntity.ok(
                reservationService.getAllReservations()
        );
    }


    // ADMIN only
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationResponse>
    updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                reservationService.updateReservationStatus(
                        id,
                        status
                )
        );
    }


    // USER only
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id) {

        reservationService.cancelReservation(id);

        return ResponseEntity.noContent().build();
    }
}

