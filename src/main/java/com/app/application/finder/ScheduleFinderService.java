package com.app.application.finder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.app.application.port.out.ScheduleRepositoryOutPort;
import com.app.domain.model.Schedule;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

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
	
	@Override
	public void validateSchedule(List<Schedule> list, LocalTime startTime, LocalTime endTime) {
		if(list.isEmpty()) 
			throw msgError();
		
		for (Schedule sc : list) {
			if (!((startTime.compareTo(sc.getStartTime()) >= 0 && endTime.compareTo(sc.getEndTime()) <= 0)
				|| (endTime.compareTo(sc.getStartTime()) > 0 && endTime.compareTo(sc.getEndTime()) < 0)
				|| (startTime.compareTo(sc.getStartTime()) < 0 && endTime.compareTo(sc.getEndTime()) > 0))) {
				throw msgError();
			}
		}
	}
	
	private BusinessException msgError() {
	    return new BusinessException(
	        BusinessErrorType.VALIDATION_ERROR,
	        "El profesional seleccionado no cuenta con disponibilidad en el horario elegido"
	    );
	}

}
