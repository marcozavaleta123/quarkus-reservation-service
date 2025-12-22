package com.app.application.port.in;

import com.app.domain.model.Schedule;

import io.smallrye.mutiny.Uni;

public interface ScheduleUseCase {
	
	Uni<String> createSchedule(Schedule schedule);

}
