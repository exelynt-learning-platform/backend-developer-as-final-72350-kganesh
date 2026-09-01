package com.ganesh.booking_system.service.impl;

import com.ganesh.booking_system.dto.ReservationRequest;
import com.ganesh.booking_system.dto.ReservationResponse;
import com.ganesh.booking_system.entity.Reservation;
import com.ganesh.booking_system.entity.Resource;
import com.ganesh.booking_system.entity.User;
import com.ganesh.booking_system.enums.ReservationStatus;
import com.ganesh.booking_system.exception.ReservationConflictException;
import com.ganesh.booking_system.exception.ReservationNotFoundException;
import com.ganesh.booking_system.exception.ResourceNotFoundException;
import com.ganesh.booking_system.exception.UnauthorizedException;
import com.ganesh.booking_system.repository.ReservationRepository;
import com.ganesh.booking_system.repository.ResourceRepository;
import com.ganesh.booking_system.repository.UserRepository;
import com.ganesh.booking_system.service.ReservationService;
import com.ganesh.booking_system.specification.ReservationSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository) {

        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // CREATE RESERVATION
    // =========================================================

    @Override
    public ReservationResponse createReservation(
            ReservationRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Resource resource =
                resourceRepository.findById(
                        request.getResourceId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: "
                                        + request.getResourceId()
                        )
                );

        // Check resource availability
        if (!resource.getAvailable()) {

            throw new RuntimeException(
                    "Resource is currently unavailable"
            );
        }

        // Validate time
        if (!request.getStartTime()
                .isBefore(request.getEndTime())) {

            throw new RuntimeException(
                    "Start time must be before end time"
            );
        }

        // Active reservation statuses
        List<ReservationStatus> activeStatuses =
                List.of(
                        ReservationStatus.PENDING,
                        ReservationStatus.CONFIRMED
                );

        // Check overlapping reservation
        boolean conflict =
                reservationRepository
                        .existsByResourceIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                                request.getResourceId(),
                                activeStatuses,
                                request.getEndTime(),
                                request.getStartTime()
                        );

        if (conflict) {

            throw new ReservationConflictException(
                    "Resource is already reserved for the selected time"
            );
        }

        // Create reservation
        Reservation reservation =
                new Reservation();

        reservation.setUser(user);
        reservation.setResource(resource);

        reservation.setStartTime(
                request.getStartTime()
        );

        reservation.setEndTime(
                request.getEndTime()
        );

        // Price is taken from resource
        reservation.setPrice(
                resource.getPrice()
        );

        // New reservation starts as PENDING
        reservation.setStatus(
                ReservationStatus.PENDING
        );

        Reservation savedReservation =
                reservationRepository.save(
                        reservation
                );

        return mapToResponse(
                savedReservation
        );
    }


    // =========================================================
    // GET MY RESERVATIONS
    // =========================================================

    @Override
    public List<ReservationResponse> getMyReservations() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return reservationRepository
                .findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET RESERVATION BY ID
    // =========================================================

    @Override
    public ReservationResponse getReservationById(
            Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id
                                )
                        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        // ADMIN can view any reservation
        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        // USER can view only their own reservation
        if (!isAdmin &&
                !reservation.getUser()
                        .getUsername()
                        .equals(username)) {

            throw new UnauthorizedException(
                    "You are not authorized to view this reservation"
            );
        }

        return mapToResponse(
                reservation
        );
    }


    // =========================================================
    // GET ALL RESERVATIONS
    // FILTER + PAGINATION + SORTING
    // =========================================================

    @Override
    public Page<ReservationResponse> getAllReservations(
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        ReservationStatus reservationStatus = null;

        if (status != null && !status.isBlank()) {

            try {

                reservationStatus =
                        ReservationStatus.valueOf(
                                status.toUpperCase()
                        );

            } catch (IllegalArgumentException e) {

                throw new RuntimeException(
                        "Invalid reservation status: " + status
                );
            }
        }

        Specification<Reservation> specification =
                Specification
                        .where(
                                ReservationSpecification.hasStatus(
                                        reservationStatus
                                )
                        )
                        .and(
                                ReservationSpecification.hasMinPrice(
                                        minPrice
                                )
                        )
                        .and(
                                ReservationSpecification.hasMaxPrice(
                                        maxPrice
                                )
                        );

        Page<Reservation> reservations =
                reservationRepository.findAll(
                        specification,
                        pageable
                );

        return reservations.map(
                this::mapToResponse
        );
    }


    // =========================================================
    // UPDATE RESERVATION STATUS
    // ADMIN ONLY
    // =========================================================

    @Override
    public ReservationResponse updateReservationStatus(
            Long id,
            String status) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id
                                )
                        );

        ReservationStatus reservationStatus;

        try {

            reservationStatus =
                    ReservationStatus.valueOf(
                            status.toUpperCase()
                    );

        } catch (IllegalArgumentException e) {

            throw new RuntimeException(
                    "Invalid reservation status: "
                            + status
            );
        }

        reservation.setStatus(
                reservationStatus
        );

        Reservation updatedReservation =
                reservationRepository.save(
                        reservation
                );

        return mapToResponse(
                updatedReservation
        );
    }


    // =========================================================
    // CANCEL RESERVATION
    // =========================================================

    @Override
    public void cancelReservation(
            Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id
                                )
                        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        // USER can cancel only their own reservation
        if (!reservation.getUser()
                .getUsername()
                .equals(username)) {

            throw new UnauthorizedException(
                    "You are not authorized to cancel this reservation"
            );
        }

        reservation.setStatus(
                ReservationStatus.CANCELLED
        );

        reservationRepository.save(
                reservation
        );
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),

                reservation.getResource().getId(),
                reservation.getResource().getName(),

                reservation.getUser().getId(),
                reservation.getUser().getUsername(),

                reservation.getStartTime(),
                reservation.getEndTime(),

                reservation.getPrice(),
                reservation.getStatus(),

                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}