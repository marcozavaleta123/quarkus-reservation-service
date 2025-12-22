package com.app.application.port.out;

import com.app.domain.model.Schedule;

import io.smallrye.mutiny.Uni;

public interface ScheduleRepositoryOutPort {
	
	Uni<String> save(Schedule schedule);

}
