package com.ganesh.booking_system.controller;

import com.ganesh.booking_system.dto.ReservationRequest;
import com.ganesh.booking_system.dto.ReservationResponse;
import com.ganesh.booking_system.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService = reservationService;
    }


    // =========================================================
    // USER - CREATE RESERVATION
    // =========================================================

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


    // =========================================================
    // USER - GET MY RESERVATIONS
    // =========================================================

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationResponse>>
    getMyReservations() {

        return ResponseEntity.ok(
                reservationService.getMyReservations()
        );
    }


    // =========================================================
    // ADMIN - GET ALL RESERVATIONS
    // FILTER + PAGINATION + SORTING
    // =========================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationResponse>>
    getAllReservations(

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        return ResponseEntity.ok(
                reservationService.getAllReservations(
                        status,
                        minPrice,
                        maxPrice,
                        pageable
                )
        );
    }


    // =========================================================
    // USER + ADMIN - GET RESERVATION BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservationResponse>
    getReservationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservationService.getReservationById(id)
        );
    }


    // =========================================================
    // ADMIN - UPDATE RESERVATION STATUS
    // =========================================================

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


    // =========================================================
    // USER - CANCEL OWN RESERVATION
    // =========================================================

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id) {

        reservationService.cancelReservation(id);

        return ResponseEntity.noContent().build();
    }
}
