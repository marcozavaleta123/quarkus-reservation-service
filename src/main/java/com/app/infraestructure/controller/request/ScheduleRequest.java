package com.app.infraestructure.controller.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScheduleRequest(
		@NotNull(message = "El campo date no puede ser blanco, vacio o nulo")
		LocalDate date,
		@NotNull(message = "El campo startTime no puede ser blanco, vacio o nulo")
	    LocalTime startTime,
	    @NotNull(message = "El campo endTime no puede ser blanco, vacio o nulo")
	    LocalTime endTime,
	    @NotBlank(message = "El dni del professional no puede ser blanco, vacio o nulo")
	    String professionalDni
	    ) {
	
	@AssertTrue(message = "La hora final debe ser mayor que la hora de inicio")
    public boolean isValidTimeRange() {
        return endTime.isAfter(startTime);
    }

}
