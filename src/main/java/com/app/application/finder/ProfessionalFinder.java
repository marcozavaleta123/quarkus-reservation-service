package com.app.application.finder;

import java.time.LocalTime;
import java.util.List;

import com.app.domain.model.Professional;
import com.app.domain.model.Schedule;

import io.smallrye.mutiny.Uni;

public interface ProfessionalFinder {
	
	Uni<Professional> findByDni(String dni);
	
	void validateSchedule(List<Schedule> list, LocalTime startTime, LocalTime endTime);

}
