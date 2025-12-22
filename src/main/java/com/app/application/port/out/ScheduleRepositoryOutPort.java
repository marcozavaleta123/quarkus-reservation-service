package com.app.application.port.out;

import java.time.LocalDate;
import java.util.List;

import com.app.domain.model.Schedule;

import io.smallrye.mutiny.Uni;

public interface ScheduleRepositoryOutPort {
	
	Uni<Long> save(Schedule schedule, Long professionalId);
	
	Uni<List<Schedule>> findByProfessionalIdAndDate(long professionalId, LocalDate date);

}
