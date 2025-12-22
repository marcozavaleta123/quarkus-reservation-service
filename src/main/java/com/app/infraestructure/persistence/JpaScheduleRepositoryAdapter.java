package com.app.infraestructure.persistence;

import static java.util.Objects.isNull;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.ScheduleRepositoryOutPort;
import com.app.domain.model.Schedule;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;
import com.app.infraestructure.persistence.entity.ProfessionalEntity;
import com.app.infraestructure.persistence.entity.ScheduleEntity;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class JpaScheduleRepositoryAdapter implements ScheduleRepositoryOutPort {
	
	private final ModelMapper modelMapper;

	@Override
	public Uni<String> save(Schedule schedule) {
		return ProfessionalEntity.find("dni = ?1", schedule.getProfessionalDni()).firstResult()
				.flatMap(professional -> {
					if (isNull(professional)) {
						return Uni.createFrom().failure(new BusinessException(BusinessErrorType.VALIDATION_ERROR,
								"el DNI del profesional ingresado no se encuentra registrado"));
					}

					ProfessionalEntity professionalEntity = (ProfessionalEntity) professional;

					return ScheduleEntity.<ScheduleEntity>find("professionalEntity.id = ?1 and date = ?2 and status = ?3", professionalEntity.getId(), schedule.getDate(), true).list()
							.flatMap(list -> {
								for (ScheduleEntity sc : list) {
									if ((schedule.getStartTime().compareTo(sc.getStartTime()) >= 0 && schedule.getStartTime().compareTo(sc.getEndTime()) <= 0)
										|| (schedule.getEndTime().compareTo(sc.getStartTime()) > 0 && schedule.getEndTime().compareTo(sc.getEndTime()) < 0)) {
										return Uni.createFrom().failure(new BusinessException(
												BusinessErrorType.VALIDATION_ERROR,
												"el profesional seleccionado no cuenta con disponibilidad en el horario elegido"));
									}
								}

								ScheduleEntity entity = modelMapper.map(schedule, ScheduleEntity.class);
								entity.setProfessionalEntity(professionalEntity);
								entity.setStatus(true);

								return ScheduleEntity.persist(entity).flatMap(s -> {
									return Uni.createFrom()
											.item("Al profesional cuyo DNI es : " + schedule.getProfessionalDni()
													+ " se le asignó correctamente el horario : " + schedule.getDate()
													+ " hora de inicio : " + schedule.getStartTime() + " - hora fin : "
													+ schedule.getEndTime());
								});
							});
				});
	}

}
