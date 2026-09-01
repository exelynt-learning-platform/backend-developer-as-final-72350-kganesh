package com.ganesh.booking_system.service.impl;

import com.ganesh.booking_system.dto.ReservationRequest;
import com.ganesh.booking_system.dto.ReservationResponse;
import com.ganesh.booking_system.entity.Reservation;
import com.ganesh.booking_system.entity.Resource;
import com.ganesh.booking_system.entity.User;
import com.ganesh.booking_system.enums.ReservationStatus;
import com.ganesh.booking_system.repository.ReservationRepository;
import com.ganesh.booking_system.repository.ResourceRepository;
import com.ganesh.booking_system.repository.UserRepository;
import com.ganesh.booking_system.service.ReservationService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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


    @Override
    public ReservationResponse createReservation(
            ReservationRequest request) {

        // Get currently authenticated username
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();


        // Find the logged-in user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );


        // Find the resource
        Resource resource = resourceRepository
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new RuntimeException(
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


        // Validate start and end time
        if (!request.getStartTime()
                .isBefore(request.getEndTime())) {

            throw new RuntimeException(
                    "Start time must be before end time"
            );
        }

        // Check for overlapping reservation
        List<ReservationStatus> activeStatuses = 
                        List.of( 
                                ReservationStatus.PENDING, 
                                ReservationStatus.CONFIRMED 
                        ); 
        
        boolean conflict = 
                        reservationRepository
                                .existsByResourceIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan( 
                                        request.getResourceId(), 
                                        activeStatuses, 
                                        request.getEndTime(), 
                                        request.getStartTime() );

        if (conflict) {

        throw new RuntimeException(
                "Resource is already reserved for the selected time"
        );
        }

        // Create reservation
        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setResource(resource);

        reservation.setStartTime(
                request.getStartTime()
        );

        reservation.setEndTime(
                request.getEndTime()
        );

        // For now, use resource price
        reservation.setPrice(
                resource.getPrice()
        );

        reservation.setStatus(
                ReservationStatus.PENDING
        );


        // Save reservation
        Reservation savedReservation =
                reservationRepository.save(reservation);


        return mapToResponse(savedReservation);
    }


    @Override
    public List<ReservationResponse> getMyReservations() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
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


    @Override
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found with id: " + id
                                )
                        );

        // ADMIN can view any reservation
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        // If USER, check ownership
        if (authentication.getAuthorities().stream()
                .noneMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"))) {

            if (!reservation.getUser()
                    .getUsername()
                    .equals(username)) {

                throw new RuntimeException(
                        "You are not authorized to view this reservation"
                );
            }
        }

        return mapToResponse(reservation);
    }


    @Override
    public List<ReservationResponse> getAllReservations() {

        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public ReservationResponse updateReservationStatus(
            Long id,
            String status) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
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
                    "Invalid reservation status: " + status
            );
        }


        reservation.setStatus(reservationStatus);

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(updatedReservation);
    }


    @Override
    public void cancelReservation(Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found with id: " + id
                                )
                        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        // USER can cancel only their own reservation
        if (!reservation.getUser()
                .getUsername()
                .equals(username)) {

            throw new RuntimeException(
                    "You are not authorized to cancel this reservation"
            );
        }

        reservation.setStatus(
                ReservationStatus.CANCELLED
        );

        reservationRepository.save(reservation);
    }

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
