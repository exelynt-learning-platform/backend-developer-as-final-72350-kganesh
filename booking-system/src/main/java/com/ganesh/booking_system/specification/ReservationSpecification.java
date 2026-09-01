package com.ganesh.booking_system.specification;

import com.ganesh.booking_system.entity.Reservation;
import com.ganesh.booking_system.enums.ReservationStatus;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ReservationSpecification {

    public static Specification<Reservation> hasStatus(
            ReservationStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }


    public static Specification<Reservation> hasMinPrice(
            BigDecimal minPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }


    public static Specification<Reservation> hasMaxPrice(
            BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }
}
