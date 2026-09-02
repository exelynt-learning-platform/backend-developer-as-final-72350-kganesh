package com.ganesh.booking_system.validation;

import com.ganesh.booking_system.dto.ReservationRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationTimeValidator
        implements ConstraintValidator<ValidReservationTime, ReservationRequest> {

    @Override
    public boolean isValid(
            ReservationRequest request,
            ConstraintValidatorContext context) {

        if (request == null) {
            return true;
        }

        if (request.getStartTime() == null ||
                request.getEndTime() == null) {
            return true;
        }

        if (request.getEndTime().isAfter(request.getStartTime())) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        context.buildConstraintViolationWithTemplate(
                "End time must be after start time"
        )
        .addPropertyNode("endTime")
        .addConstraintViolation();

        return false;
    }

}
