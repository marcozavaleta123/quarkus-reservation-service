package com.app.application.service;

import java.util.List;

import com.app.application.finder.ProfessionalFinder;
import com.app.application.finder.ScheduleFinder;
import com.app.application.port.in.ScheduleUseCase;
import com.app.application.port.out.ScheduleRepositoryOutPort;
import com.app.domain.model.Schedule;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ScheduleService implements ScheduleUseCase {
	
	private final ProfessionalFinder professionalFinder;
	private final ScheduleFinder scheduleFinder;
	private final ScheduleRepositoryOutPort scheduleRepositoryOutPort;
	
	@WithTransaction
	@Override
	public Uni<String> createSchedule(Schedule schedule) {
		return professionalFinder.findByDni(schedule.getProfessionalDni()).flatMap(professional -> {
			return scheduleFinder.findByProfessionalIdAndDate(professional.getId(), schedule.getDate())
					.invoke(list -> validateSchedule(list, schedule))
					.flatMap(list -> scheduleRepositoryOutPort.save(schedule, professional.getId()))
                    .map(id ->
                           "Al profesional cuyo DNI es : " + schedule.getProfessionalDni()
                         + " se le asignó correctamente el horario : " + schedule.getDate()
                         + " hora de inicio : " + schedule.getStartTime() + " - hora fin : " + schedule.getEndTime()
                     );
		});
	}
	
	private void validateSchedule(List<Schedule> list, Schedule schedule) {
		for (Schedule sc : list) {
			if ((schedule.getStartTime().compareTo(sc.getStartTime()) >= 0 && schedule.getStartTime().compareTo(sc.getEndTime()) <= 0)
				|| (schedule.getEndTime().compareTo(sc.getStartTime()) > 0 && schedule.getEndTime().compareTo(sc.getEndTime()) < 0)
				|| (schedule.getStartTime().compareTo(sc.getStartTime()) < 0 && schedule.getEndTime().compareTo(sc.getEndTime()) > 0)) {
				throw new BusinessException(
		                BusinessErrorType.VALIDATION_ERROR,
		                "El profesional seleccionado no cuenta con disponibilidad en el horario elegido"
		            );
			}
		}
	}

}
