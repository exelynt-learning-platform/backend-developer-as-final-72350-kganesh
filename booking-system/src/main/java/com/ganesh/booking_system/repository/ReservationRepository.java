package com.ganesh.booking_system.repository;

import com.ganesh.booking_system.entity.Reservation;
import com.ganesh.booking_system.entity.User;
import com.ganesh.booking_system.enums.ReservationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long>,
                JpaSpecificationExecutor<Reservation> {

    List<Reservation> findByUser(User user);

    boolean existsByResourceIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long resourceId,
            List<ReservationStatus> statuses,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}