package com.app.infraestructure.controller.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;

public record BookingRequest(
		LocalDate date,
	    LocalTime startTime,
	    LocalTime endTime,
	    String professionalDni,
	    String clientDni) {
	
	@AssertTrue(message = "La hora final debe ser mayor que la hora de inicio")
    public boolean isValidTimeRange() {
        return endTime.isAfter(startTime);
    }

}
