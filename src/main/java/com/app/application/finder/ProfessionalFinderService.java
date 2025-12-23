package com.app.application.finder;

import com.app.application.port.out.ProfessionalRepositoryOutPort;
import com.app.domain.model.Professional;
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

}
