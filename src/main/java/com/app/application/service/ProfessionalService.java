package com.app.application.service;

import static java.util.Objects.nonNull;

import java.util.List;

import com.app.application.port.in.ProfessionalUseCase;
import com.app.application.port.out.ProfessionalRepositoryOutPort;
import com.app.domain.model.Professional;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProfessionalService implements ProfessionalUseCase {
	
	private final ProfessionalRepositoryOutPort professionalRepositoryOutPort;

	@Override
	public Uni<Professional> createProfessional(Professional professional) {
		return professionalRepositoryOutPort.findByDni(professional.getDni())
				.flatMap(existingProfessional-> {
                    if (nonNull(existingProfessional)) {
                        return Uni.createFrom().failure(
                                new BusinessException(BusinessErrorType.VALIDATION_ERROR, "el DNI del profesional ingresado ya se encuentra registrado")
                        );
                    }
                    
                    return professionalRepositoryOutPort.save(professional);
                });
	}

	@Override
	public Uni<List<Professional>> getProfessionals() {
		return professionalRepositoryOutPort.findAll();
	}

	@Override
	public Uni<Professional> getProfessionalByDni(String dni) {
		return professionalRepositoryOutPort.findByDni(dni)
				.onItem().ifNull().failWith(
		                new BusinessException(
		                    BusinessErrorType.VALIDATION_ERROR,
		                    "El DNI del profesional ingresado no se encuentra registrado"
		                )
		            );
	}

	@Override
	public Uni<String> updateProfessionalStatus(String dni, boolean status) {
		return professionalRepositoryOutPort.updateStatus(dni, status)
				.flatMap(b -> {
    		        if (b) {
    		        	if(status)
    		        	  return Uni.createFrom().item("El estado del profesional se activó correctamente");
    		        	else
    		        	  return Uni.createFrom().item("El estado del profesional se desactivó correctamente");
    		        }
    		        return Uni.createFrom().item("El DNI ingresado no se encuentra registrado");
    		    });
	}

}
