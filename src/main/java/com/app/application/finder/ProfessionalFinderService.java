package com.app.application.finder;

import java.time.LocalTime;
import java.util.List;

import com.app.application.port.out.ProfessionalRepositoryOutPort;
import com.app.domain.model.Professional;
import com.app.domain.model.Schedule;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProfessionalFinderService implements ProfessionalFinder {
	
	private final ProfessionalRepositoryOutPort professionalRepositoryOutPort;
	
	@Override
	public Uni<Professional> findByDni(String dni) {
		return professionalRepositoryOutPort.findByDni(dni)
				.onItem().ifNull().failWith(
		                new BusinessException(
		                    BusinessErrorType.VALIDATION_ERROR,
		                    "El DNI del profesional ingresado no se encuentra registrado"
		                )
		            );
	}

	@Override
	public void validateSchedule(List<Schedule> list, LocalTime startTime, LocalTime endTime) {
		if(list.isEmpty()) 
			throw new BusinessException(
	                BusinessErrorType.VALIDATION_ERROR,
	                "El profesional seleccionado no cuenta con disponibilidad en el horario elegido"
	            );
		
		for (Schedule sc : list) {
			if (!((startTime.compareTo(sc.getStartTime()) >= 0 && endTime.compareTo(sc.getEndTime()) <= 0)
				|| (endTime.compareTo(sc.getStartTime()) > 0 && endTime.compareTo(sc.getEndTime()) < 0)
				|| (startTime.compareTo(sc.getStartTime()) < 0 && endTime.compareTo(sc.getEndTime()) > 0))) {
				throw new BusinessException(
		                BusinessErrorType.VALIDATION_ERROR,
		                "El profesional seleccionado no cuenta con disponibilidad en el horario elegido"
		            );
			}
		}
	}

}
