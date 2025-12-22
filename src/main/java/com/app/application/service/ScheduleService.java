package com.app.application.service;

import com.app.application.port.in.ScheduleUseCase;
import com.app.application.port.out.ScheduleRepositoryOutPort;
import com.app.domain.model.Schedule;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ScheduleService implements ScheduleUseCase {
	
	private final ProfessionalService professionalService;
	private final ScheduleRepositoryOutPort scheduleRepositoryOutPort;
	
	@Override
	public Uni<String> createSchedule(Schedule schedule) {
		return professionalService.getProfessionalByDni(schedule.getProfessionalDni())
				.flatMap(professional -> {
					return scheduleRepositoryOutPort.findByProfessionalIdAndDate(professional.getId(), schedule.getDate())
							.flatMap(list -> {
								for (Schedule sc : list) {
									if ((schedule.getStartTime().compareTo(sc.getStartTime()) >= 0 && schedule.getStartTime().compareTo(sc.getEndTime()) <= 0)
										|| (schedule.getEndTime().compareTo(sc.getStartTime()) > 0 && schedule.getEndTime().compareTo(sc.getEndTime()) < 0)
										|| (schedule.getStartTime().compareTo(sc.getStartTime()) < 0 && schedule.getEndTime().compareTo(sc.getEndTime()) > 0)) {
										return Uni.createFrom().failure(new BusinessException(
												BusinessErrorType.VALIDATION_ERROR,
												"el profesional seleccionado no cuenta con disponibilidad en el horario elegido"));
									}
								}

								return scheduleRepositoryOutPort.save(schedule, professional.getId()).flatMap(id -> {
									if(id != null) {
										return Uni.createFrom()
												.item("Al profesional cuyo DNI es : " + schedule.getProfessionalDni()
														+ " se le asignó correctamente el horario : " + schedule.getDate()
														+ " hora de inicio : " + schedule.getStartTime() + " - hora fin : "
														+ schedule.getEndTime());
									} else {
										return Uni.createFrom().failure(new BusinessException(
												BusinessErrorType.VALIDATION_ERROR,
												"error al registrar el horario"));
									}
									
								});
							});
				});
	}

}
