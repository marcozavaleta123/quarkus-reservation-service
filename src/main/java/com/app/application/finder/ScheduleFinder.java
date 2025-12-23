package com.app.application.finder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.app.domain.model.Schedule;

import io.smallrye.mutiny.Uni;

public interface ScheduleFinder {

	Uni<List<Schedule>> findByProfessionalIdAndDate(Long professionalId, LocalDate date);
	
	void validateSchedule(List<Schedule> list, LocalTime startTime, LocalTime endTime);
	
}
