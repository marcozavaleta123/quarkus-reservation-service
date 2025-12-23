package com.app.application.finder;

import java.time.LocalDate;
import java.util.List;

import com.app.application.port.out.ScheduleRepositoryOutPort;
import com.app.domain.model.Schedule;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ScheduleFinderService implements ScheduleFinder {
	
	private final ScheduleRepositoryOutPort scheduleRepositoryOutPort;

	@Override
	public Uni<List<Schedule>> findByProfessionalIdAndDate(Long professionalId, LocalDate date) {
		return scheduleRepositoryOutPort.findByProfessionalIdAndDate(professionalId, date);
	}

}
