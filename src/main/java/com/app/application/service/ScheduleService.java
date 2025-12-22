package com.app.application.service;

import com.app.application.port.in.ScheduleUseCase;
import com.app.application.port.out.ScheduleRepositoryOutPort;
import com.app.domain.model.Schedule;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ScheduleService implements ScheduleUseCase {
	
	private final ScheduleRepositoryOutPort scheduleRepositoryOutPort;

	@Override
	public Uni<String> createSchedule(Schedule schedule) {
		return scheduleRepositoryOutPort.save(schedule);
	}

}
